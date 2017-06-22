/* 
 * Copyright 2012-2016 bambooCORE, greenstep of copyright Chen Xin Nien
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * -----------------------------------------------------------------------
 * 
 * author: 	Chen Xin Nien
 * contact: chen.xin.nien@gmail.com
 * 
 */
package com.netsteadfast.greenstep.service.impl;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.netsteadfast.greenstep.base.dao.IBaseDAO;
import com.netsteadfast.greenstep.base.service.BaseService;
import com.netsteadfast.greenstep.dao.IUserRoleDAO;
import com.netsteadfast.greenstep.po.hbm.TbUserRole;
import com.netsteadfast.greenstep.service.IUserRoleService;
import com.netsteadfast.greenstep.vo.UserRoleVO;

@Service("core.service.UserRoleService")
@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
public class UserRoleServiceImpl extends BaseService<UserRoleVO, TbUserRole, String> implements IUserRoleService<UserRoleVO, TbUserRole, String> {
	protected Logger logger=Logger.getLogger(UserRoleServiceImpl.class);
	private IUserRoleDAO<TbUserRole, String> userRoleDAO;
	
	public UserRoleServiceImpl() {
		super();
	}

	public IUserRoleDAO<TbUserRole, String> getUserRoleDAO() {
		return userRoleDAO;
	}

	@Autowired
	@Resource(name="core.dao.UserRoleDAO")
	@Required		
	public void setUserRoleDAO(
			IUserRoleDAO<TbUserRole, String> userRoleDAO) {
		this.userRoleDAO = userRoleDAO;
	}

	@Override
	protected IBaseDAO<TbUserRole, String> getBaseDataAccessObject() {
		return userRoleDAO;
	}

	@Override
	public String getMapperIdPo2Vo() {		
		return MAPPER_ID_PO2VO;
	}

	@Override
	public String getMapperIdVo2Po() {
		return MAPPER_ID_VO2PO;
	}

}
