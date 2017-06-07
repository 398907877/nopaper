/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.sys.service;

import java.math.BigDecimal;
import java.util.*;

import com.thinkgem.jeesite.modules.sys.dao.RewardDetailDao;
import com.thinkgem.jeesite.modules.sys.entity.*;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.security.Digests;
import com.thinkgem.jeesite.common.security.shiro.session.SessionDAO;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.utils.CacheUtils;
import com.thinkgem.jeesite.common.utils.Encodes;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.Servlets;
import com.thinkgem.jeesite.modules.sys.dao.MenuDao;
import com.thinkgem.jeesite.modules.sys.dao.RoleDao;
import com.thinkgem.jeesite.modules.sys.dao.UserDao;
import com.thinkgem.jeesite.modules.sys.security.SystemAuthorizingRealm;
import com.thinkgem.jeesite.modules.sys.utils.LogUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 * @author ThinkGem
 * @version 2013-12-05
 */
@Service
@Transactional(readOnly = true)
public class SystemService extends BaseService implements InitializingBean {
	
	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	public static final int SALT_SIZE = 8;
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private RewardDetailDao rewardDetailDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private MenuDao menuDao;
	@Autowired
	private SessionDAO sessionDao;
	@Autowired
	private SystemAuthorizingRealm systemRealm;
	
	public SessionDAO getSessionDao() {
		return sessionDao;
	}

	@Autowired
	private IdentityService identityService;

	//-- User Service --//
	
	/**
	 * 获取用户
	 * @param id
	 * @return
	 */
	public User getUser(String id) {
		return UserUtils.get(id);
	}

	/**
	 * 根据登录名获取用户
	 * @param loginName
	 * @return
	 */
	public User getUserByLoginName(String loginName) {
		return UserUtils.getByLoginName(loginName);
	}
	
	public Page<User> findUser(Page<User> page, User user) {
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		user.getSqlMap().put("dsf", dataScopeFilter(user.getCurrentUser(), "o", "a"));
		// 设置分页参数
		user.setPage(page);
		// 执行分页查询
		page.setList(userDao.findList(user));
		return page;
	}

	/**
	 * 分页查询奖励明细
	 * @param page
	 * @param rewardDetail
	 * @return
	 */
	public Page<RewardDetail> findRewardDetail(Page<RewardDetail> page, RewardDetail rewardDetail) {
		// 设置分页参数
		rewardDetail.setPage(page);
		// 执行分页查询
		page.setList(rewardDetailDao.findList(rewardDetail));
		return page;
	}
	/**
	 * 无分页查询人员列表
	 * @param user
	 * @return
	 */
	public List<User> findUser(User user){
		// 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
		user.getSqlMap().put("dsf", dataScopeFilter(user.getCurrentUser(), "o", "a"));
		List<User> list = userDao.findList(user);
		return list;
	}

	/**
	 * 通过部门ID获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<User> findUserByOfficeId(String officeId) {
		List<User> list = (List<User>)CacheUtils.get(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + officeId);
		if (list == null){
			User user = new User();
			user.setOffice(new Office(officeId));
			list = userDao.findUserByOfficeId(user);
			CacheUtils.put(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + officeId, list);
		}
		return list;
	}

	@Transactional(readOnly = false)
	public void saveUser(User user) {
		if (StringUtils.isBlank(user.getId())){
			user.preInsert();
			userDao.insert(user);
		}else{
			// 清除原用户机构用户缓存
			User oldUser = userDao.get(user.getId());
			if (oldUser.getOffice() != null && oldUser.getOffice().getId() != null){
				CacheUtils.remove(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + oldUser.getOffice().getId());
			}
			// 更新用户数据
			user.preUpdate();
			userDao.update(user);
		}
		if (StringUtils.isNotBlank(user.getId())){
			// 更新用户与角色关联
			userDao.deleteUserRole(user);
			if (user.getRoleList() != null && user.getRoleList().size() > 0){
				userDao.insertUserRole(user);
			}else{
				throw new ServiceException(user.getLoginName() + "没有设置角色！");
			}
			// 将当前用户同步到Activiti
			saveActivitiUser(user);
			// 清除用户缓存
			UserUtils.clearCache(user);
//			// 清除权限缓存
//			systemRealm.clearAllCachedAuthorizationInfo();
		}
	}
	@Transactional(readOnly = false)
	public void saveRegistUser(User user) {
		if (StringUtils.isBlank(user.getId())){
			user.setGwf(new BigDecimal(user.getLevel())
					.multiply(new BigDecimal("0.48" )).toString());

			user.getRoleList().add(new Role("6"));
			user.preInsert();
			userDao.insert(user);
			String reReward = "0.08";
			String reLdReward = "0.05";
			if(Integer.valueOf(user.getLevel())>500){//相应级别才有对碰
				reReward = "0.1";
			}
			//推荐奖励
			countReward(UserUtils.getUser(),reReward,user.getLevel(),"tj");

			List<User> sblings = userDao.getByLinkperson(user);
			if(sblings!=null && sblings.size() == 2){//处理领导
				String[] linkpersons = user.getLinkPersons().split(",");
				int pCount = 0;
				User firstParent = userDao.getByLoginName(new User(null,
						linkpersons[linkpersons.length-1]));
				countReward(firstParent,reReward,user.getLevel(),"dp");//对碰奖励
				for (int i = linkpersons.length-2; i > 0; i--) {
					String linkperson = linkpersons[i];
					User parent = userDao.getByLoginName(new User(null,linkperson));
					if(pCount == 0){//领导奖励
						countReward(parent,reLdReward,user.getLevel(),"ld");
					}else if (pCount == 1){
						if(Integer.valueOf(parent.getLevel())>=500){//相应级别才有奖励
							countReward(parent,reLdReward,user.getLevel(),"ld");
						}
					}else if(pCount == 2){
						if(Integer.valueOf(parent.getLevel())>=1000){
							countReward(parent,reLdReward,user.getLevel(),"ld");
						}
					}else if(pCount == 3){
						if(Integer.valueOf(parent.getLevel())>=2000){
							countReward(parent,reLdReward,user.getLevel(),"ld");
						}
					}else if(pCount == 4){
						if(Integer.valueOf(parent.getLevel())>=5000){
							countReward(parent,reLdReward,user.getLevel(),"ld");
						}
					}else{
						break;
					}
					pCount ++;
				}
			}
		}else{
			// 清除原用户机构用户缓存
			User oldUser = userDao.get(user.getId());
			if (oldUser.getOffice() != null && oldUser.getOffice().getId() != null){
				CacheUtils.remove(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID_ + oldUser.getOffice().getId());
			}
			// 更新用户数据
			user.preUpdate();
			userDao.update(user);
		}
		if (StringUtils.isNotBlank(user.getId())){
			// 更新用户与角色关联
			userDao.deleteUserRole(user);
			if (user.getRoleList() != null && user.getRoleList().size() > 0){
				userDao.insertUserRole(user);
			}else{
				throw new ServiceException(user.getLoginName() + "没有设置角色！");
			}
			// 将当前用户同步到Activiti
			saveActivitiUser(user);
			// 清除用户缓存
			UserUtils.clearCache(user);
//			// 清除权限缓存
//			systemRealm.clearAllCachedAuthorizationInfo();
		}
	}

	/**
	 *
	 * @param rewardUser  奖励对象
	 * @param percent 百分比
	 * @param point 分数
	 */
	private void countReward(User rewardUser,String percent,String point,String type){
		BigDecimal rewardPoint = new BigDecimal(percent).multiply(new BigDecimal(point));
		BigDecimal wkf = rewardPoint
				.multiply(new BigDecimal("0.9"));
		BigDecimal yxf = rewardPoint
				.multiply(new BigDecimal("0.05"));
		BigDecimal gwf = rewardPoint
				.multiply(new BigDecimal("0.05"));
		rewardUser.setWkf(new BigDecimal(rewardUser.getWkf()).add(wkf).toString());
		rewardUser.setYxf(new BigDecimal(rewardUser.getYxf()).add(yxf).toString());
		rewardUser.setGwf(new BigDecimal(rewardUser.getGwf()).add(gwf).toString());

		RewardDetail rewardDetail = new RewardDetail();
		rewardDetail.setUser(rewardUser);
		rewardDetail.setGwf(gwf.toString());
		rewardDetail.setWkf(wkf.toString());
		rewardDetail.setYxf(yxf.toString());
		if("ld".equals(type)){
			rewardDetail.setTj("0");
			rewardDetail.setLd(rewardPoint.toString());
			rewardDetail.setDp("0");
		}
		if("dp".equals(type)){
			rewardDetail.setTj("0");
			rewardDetail.setLd("0");
			rewardDetail.setDp(rewardPoint.toString());
		}
		if("tj".equals(type)){
			rewardDetail.setTj(rewardPoint.toString());
			rewardDetail.setDp("0");
			rewardDetail.setLd("0");
		}
		rewardDetail.preInsert();
		rewardDetailDao.insert(rewardDetail);
		rewardUser.preUpdate();
		userDao.update(rewardUser);
	}
	@Transactional(readOnly = false)
	public void updateUserInfo(User user) {
		user.preUpdate();
		userDao.updateUserInfo(user);
		// 清除用户缓存
		UserUtils.clearCache(user);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
	}
	
	@Transactional(readOnly = false)
	public void deleteUser(User user) {
		userDao.delete(user);
		// 同步到Activiti
		deleteActivitiUser(user);
		// 清除用户缓存
		UserUtils.clearCache(user);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
	}
	
	@Transactional(readOnly = false)
	public void updatePasswordById(String id, String loginName, String newPassword) {
		User user = new User(id);
		user.setPassword(entryptPassword(newPassword));
		userDao.updatePasswordById(user);
		// 清除用户缓存
		user.setLoginName(loginName);
		UserUtils.clearCache(user);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
	}
	
	@Transactional(readOnly = false)
	public void updateUserLoginInfo(User user) {
		// 保存上次登录信息
		user.setOldLoginIp(user.getLoginIp());
		user.setOldLoginDate(user.getLoginDate());
		// 更新本次登录信息
		user.setLoginIp(StringUtils.getRemoteAddr(Servlets.getRequest()));
		user.setLoginDate(new Date());
		userDao.updateLoginInfo(user);
	}
	
	/**
	 * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
	 */
	public static String entryptPassword(String plainPassword) {
		String plain = Encodes.unescapeHtml(plainPassword);
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
		return Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword);
	}
	
	/**
	 * 验证密码
	 * @param plainPassword 明文密码
	 * @param password 密文密码
	 * @return 验证成功返回true
	 */
	public static boolean validatePassword(String plainPassword, String password) {
		String plain = Encodes.unescapeHtml(plainPassword);
		byte[] salt = Encodes.decodeHex(password.substring(0,16));
		byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
		return password.equals(Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword));
	}
	
	/**
	 * 获得活动会话
	 * @return
	 */
	public Collection<Session> getActiveSessions(){
		return sessionDao.getActiveSessions(false);
	}
	
	//-- Role Service --//
	
	public Role getRole(String id) {
		return roleDao.get(id);
	}

	public Role getRoleByName(String name) {
		Role r = new Role();
		r.setName(name);
		return roleDao.getByName(r);
	}
	
	public Role getRoleByEnname(String enname) {
		Role r = new Role();
		r.setEnname(enname);
		return roleDao.getByEnname(r);
	}
	
	public List<Role> findRole(Role role){
		return roleDao.findList(role);
	}
	
	public List<Role> findAllRole(){
		return UserUtils.getRoleList();
	}
	
	@Transactional(readOnly = false)
	public void saveRole(Role role) {
		if (StringUtils.isBlank(role.getId())){
			role.preInsert();
			roleDao.insert(role);
			// 同步到Activiti
			saveActivitiGroup(role);
		}else{
			role.preUpdate();
			roleDao.update(role);
		}
		// 更新角色与菜单关联
		roleDao.deleteRoleMenu(role);
		if (role.getMenuList().size() > 0){
			roleDao.insertRoleMenu(role);
		}
		// 更新角色与部门关联
		roleDao.deleteRoleOffice(role);
		if (role.getOfficeList().size() > 0){
			roleDao.insertRoleOffice(role);
		}
		// 同步到Activiti
		saveActivitiGroup(role);
		// 清除用户角色缓存
		UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
	}

	@Transactional(readOnly = false)
	public void deleteRole(Role role) {
		roleDao.delete(role);
		// 同步到Activiti
		deleteActivitiGroup(role);
		// 清除用户角色缓存
		UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
	}
	
	@Transactional(readOnly = false)
	public Boolean outUserInRole(Role role, User user) {
		List<Role> roles = user.getRoleList();
		for (Role e : roles){
			if (e.getId().equals(role.getId())){
				roles.remove(e);
				saveUser(user);
				return true;
			}
		}
		return false;
	}
	
	@Transactional(readOnly = false)
	public User assignUserToRole(Role role, User user) {
		if (user == null){
			return null;
		}
		List<String> roleIds = user.getRoleIdList();
		if (roleIds.contains(role.getId())) {
			return null;
		}
		user.getRoleList().add(role);
		saveUser(user);
		return user;
	}

	//-- Menu Service --//
	
	public Menu getMenu(String id) {
		return menuDao.get(id);
	}

	public List<Menu> findAllMenu(){
		return UserUtils.getMenuList();
	}
	
	@Transactional(readOnly = false)
	public void saveMenu(Menu menu) {
		
		// 获取父节点实体
		menu.setParent(this.getMenu(menu.getParent().getId()));
		
		// 获取修改前的parentIds，用于更新子节点的parentIds
		String oldParentIds = menu.getParentIds(); 
		
		// 设置新的父节点串
		menu.setParentIds(menu.getParent().getParentIds()+menu.getParent().getId()+",");

		// 保存或更新实体
		if (StringUtils.isBlank(menu.getId())){
			menu.preInsert();
			menuDao.insert(menu);
		}else{
			menu.preUpdate();
			menuDao.update(menu);
		}
		
		// 更新子节点 parentIds
		Menu m = new Menu();
		m.setParentIds("%,"+menu.getId()+",%");
		List<Menu> list = menuDao.findByParentIdsLike(m);
		for (Menu e : list){
			e.setParentIds(e.getParentIds().replace(oldParentIds, menu.getParentIds()));
			menuDao.updateParentIds(e);
		}
		// 清除用户菜单缓存
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
		// 清除日志相关缓存
		CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
	}

	@Transactional(readOnly = false)
	public void updateMenuSort(Menu menu) {
		menuDao.updateSort(menu);
		// 清除用户菜单缓存
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
		// 清除日志相关缓存
		CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
	}

	@Transactional(readOnly = false)
	public void deleteMenu(Menu menu) {
		menuDao.delete(menu);
		// 清除用户菜单缓存
		UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
		// 清除日志相关缓存
		CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
	}
	
	/**
	 * 获取Key加载信息
	 */
	public static boolean printKeyLoadMessage(){
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n======================================================================\r\n");
		sb.append("\r\n    欢迎使用 "+Global.getConfig("productName")+"  - Powered By http://wujiajun-team.com\r\n");
		sb.append("\r\n======================================================================\r\n");
		System.out.println(sb.toString());
		return true;
	}
	
	///////////////// Synchronized to the Activiti //////////////////
	
	// 已废弃，同步见：ActGroupEntityServiceFactory.java、ActUserEntityServiceFactory.java

	/**
	 * 是需要同步Activiti数据，如果从未同步过，则同步数据。
	 */
	private static boolean isSynActivitiIndetity = true;
	public void afterPropertiesSet() throws Exception {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		if (isSynActivitiIndetity){
			isSynActivitiIndetity = false;
	        // 同步角色数据
			List<Group> groupList = identityService.createGroupQuery().list();
			if (groupList.size() == 0){
			 	Iterator<Role> roles = roleDao.findAllList(new Role()).iterator();
			 	while(roles.hasNext()) {
			 		Role role = roles.next();
			 		saveActivitiGroup(role);
			 	}
			}
		 	// 同步用户数据
			List<org.activiti.engine.identity.User> userList = identityService.createUserQuery().list();
			if (userList.size() == 0){
			 	Iterator<User> users = userDao.findAllList(new User()).iterator();
			 	while(users.hasNext()) {
			 		saveActivitiUser(users.next());
			 	}
			}
		}
	}
	
	private void saveActivitiGroup(Role role) {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		String groupId = role.getEnname();
		
		// 如果修改了英文名，则删除原Activiti角色
		if (StringUtils.isNotBlank(role.getOldEnname()) && !role.getOldEnname().equals(role.getEnname())){
			identityService.deleteGroup(role.getOldEnname());
		}
		
		Group group = identityService.createGroupQuery().groupId(groupId).singleResult();
		if (group == null) {
			group = identityService.newGroup(groupId);
		}
		group.setName(role.getName());
		group.setType(role.getRoleType());
		identityService.saveGroup(group);
		
		// 删除用户与用户组关系
		List<org.activiti.engine.identity.User> activitiUserList = identityService.createUserQuery().memberOfGroup(groupId).list();
		for (org.activiti.engine.identity.User activitiUser : activitiUserList){
			identityService.deleteMembership(activitiUser.getId(), groupId);
		}

		// 创建用户与用户组关系
		List<User> userList = findUser(new User(new Role(role.getId())));
		for (User e : userList){
			String userId = e.getLoginName();//ObjectUtils.toString(user.getId());
			// 如果该用户不存在，则创建一个
			org.activiti.engine.identity.User activitiUser = identityService.createUserQuery().userId(userId).singleResult();
			if (activitiUser == null){
				activitiUser = identityService.newUser(userId);
				activitiUser.setFirstName(e.getName());
				activitiUser.setLastName(StringUtils.EMPTY);
				activitiUser.setEmail(e.getEmail());
				activitiUser.setPassword(StringUtils.EMPTY);
				identityService.saveUser(activitiUser);
			}
			identityService.createMembership(userId, groupId);
		}
	}

	public void deleteActivitiGroup(Role role) {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		if(role!=null) {
			String groupId = role.getEnname();
			identityService.deleteGroup(groupId);
		}
	}

	private void saveActivitiUser(User user) {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		String userId = user.getLoginName();//ObjectUtils.toString(user.getId());
		org.activiti.engine.identity.User activitiUser = identityService.createUserQuery().userId(userId).singleResult();
		if (activitiUser == null) {
			activitiUser = identityService.newUser(userId);
		}
		activitiUser.setFirstName(user.getName());
		activitiUser.setLastName(StringUtils.EMPTY);
		activitiUser.setEmail(user.getEmail());
		activitiUser.setPassword(StringUtils.EMPTY);
		identityService.saveUser(activitiUser);
		
		// 删除用户与用户组关系
		List<Group> activitiGroups = identityService.createGroupQuery().groupMember(userId).list();
		for (Group group : activitiGroups) {
			identityService.deleteMembership(userId, group.getId());
		}
		// 创建用户与用户组关系
		for (Role role : user.getRoleList()) {
	 		String groupId = role.getEnname();
	 		// 如果该用户组不存在，则创建一个
		 	Group group = identityService.createGroupQuery().groupId(groupId).singleResult();
            if(group == null) {
	            group = identityService.newGroup(groupId);
	            group.setName(role.getName());
	            group.setType(role.getRoleType());
	            identityService.saveGroup(group);
            }
			identityService.createMembership(userId, role.getEnname());
		}
	}

	private void deleteActivitiUser(User user) {
		if (!Global.isSynActivitiIndetity()){
			return;
		}
		if(user!=null) {
			String userId = user.getLoginName();//ObjectUtils.toString(user.getId());
			identityService.deleteUser(userId);
		}
	}
	
	///////////////// Synchronized to the Activiti end //////////////////

	/**
	 * 获取用户树数据
	 * @param
	 * @return
	 */
	public UserTreeNode getUserTreeData(){
		UserTreeNode userTree = new UserTreeNode();
		User curUser = UserUtils.getUser().getCurrentUser();
		userTree.setName(UserUtils.getUser().getLoginName());
		userTree.setId(curUser.getLoginName());
		userTree.setChildren(new ArrayList<UserTreeNode>());
		userTree.setTitle(userTree.getName()+"-"+DictUtils.getDictLabel(
				curUser.getLevel(),"USER_LEVEL", "管理员"));
		List<User> userChilds = userDao.getChildren(curUser.getLoginName(),curUser.getDbName());
		List<UserTreeNode> childs = new ArrayList<UserTreeNode>();
		for (User user :
				userChilds) {
			UserTreeNode newUserTree = new UserTreeNode();
			newUserTree.setName(user.getLoginName());
			newUserTree.setTitle(user.getName()+"-"+user.getLevel());
			newUserTree.setParentId(user.getLinkperson());
			newUserTree.setId(user.getLoginName());
			newUserTree.setChildren(new ArrayList<UserTreeNode>());
			childs.add(newUserTree);
			if(curUser.getLoginName().equals(user.getLinkperson())){
				userTree.getChildren().add(newUserTree);
			}
		}
		Set<UserTreeNode> noChildNodes = new HashSet<UserTreeNode>();
		Set<UserTreeNode> oneChildNodes = new HashSet<UserTreeNode>();
		for(UserTreeNode node1 : childs){
			int childCount = 0;
			for(UserTreeNode node2 : childs){
				if(node2.getParentId()!=null && node2.getParentId().equals(node1.getId())){

					if(node1.getChildren() == null)
						node1.setChildren(new ArrayList<UserTreeNode>());
					node1.getChildren().add(node2);
					childCount ++;
					//break;
				}
			}
			if(childCount==0){
				noChildNodes.add(node1);
			}
			if(childCount==1){
				oneChildNodes.add(node1);
			}
		}
		for (UserTreeNode noChildNode: noChildNodes) {
			noChildNode.getChildren().add(new UserTreeNode(
					"点击注册","点击注册","",noChildNode.getId()
					,"null"
			));
		}
		for (UserTreeNode oneChildNode: oneChildNodes) {
			oneChildNode.getChildren().add(new UserTreeNode(
					"点击注册","点击注册","",oneChildNode.getId()
					,"null"
			));
		}
		if(userTree.getChildren().size()==0){
			userTree.getChildren().add(new UserTreeNode(
					"点击注册","点击注册","",userTree.getId()
					,"null"
			));
		}
		if(userTree.getChildren().size()==1){
			userTree.getChildren().add(new UserTreeNode(
					"点击注册","点击注册","",userTree.getId()
					,"null"
			));
		}
		return userTree;
	}

	/**
	 * 获取用户树数据
	 * @param
	 * @return
	 */
	public UserTreeNode getUserReTreeData(){
		UserTreeNode userTree = new UserTreeNode();
		User curUser = UserUtils.getUser().getCurrentUser();
		userTree.setId(curUser.getLoginName());
		userTree.setName(UserUtils.getUser().getLoginName());
		userTree.setChildren(new ArrayList<UserTreeNode>());
		userTree.setTitle(curUser.getName()+"-"+DictUtils.getDictLabel(curUser.getLevel(),
				"USER_LEVEL",
				"管理员"));
		List<User> userChilds = userDao.getReChildren(curUser.getLoginName(),curUser.getDbName());
		List<UserTreeNode> childs = new ArrayList<UserTreeNode>();
		for (User user : userChilds) {
			UserTreeNode newUserTree = new UserTreeNode();
			newUserTree.setName(user.getLoginName());
			newUserTree.setTitle(user.getName()+"-"+user.getLevel());
			newUserTree.setParentId(user.getRePerson());
			newUserTree.setId(user.getLoginName());
			newUserTree.setChildren(new ArrayList<UserTreeNode>());
			childs.add(newUserTree);
			if(curUser.getLoginName().equals(user.getRePerson())){
				userTree.getChildren().add(newUserTree);
			}
		}
		for(UserTreeNode node1 : childs){
			for(UserTreeNode node2 : childs){
				if(node1.getParentId()!=null && node1.getParentId().equals(node2.getId())){
					if(node2.getChildren() == null)
						node2.setChildren(new ArrayList<UserTreeNode>());
					node2.getChildren().add(node1);
					break;
				}
			}
		}
		return userTree;
	}
}
