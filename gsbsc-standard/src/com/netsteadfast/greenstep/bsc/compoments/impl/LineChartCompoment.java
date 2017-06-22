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
package com.netsteadfast.greenstep.bsc.compoments.impl;

import java.util.HashMap;
import java.util.Map;

import com.netsteadfast.greenstep.bsc.compoments.BaseWorkspaceCompoment;

public class LineChartCompoment extends BaseWorkspaceCompoment {
	private static final long serialVersionUID = -8122127801060335864L;
	public static final String COMPOMENT_ID = "COMP_LINE001";
	private static final String TEMPLATE_RESOURCE = "META-INF/resource/compoments/LineChartCompoment.ftl";
	private Map<String, Object> templateParameters = new HashMap<String, Object>();
	
	public LineChartCompoment() {
		super();
	}
	
	@Override
	public String getResource() {
		return TEMPLATE_RESOURCE;
	}

	@Override
	public Map<String, Object> getParameters() {
		return templateParameters;
	}

	@Override
	public String getBody() throws Exception {
		this.loadFromId(COMPOMENT_ID);
		this.getModel().setWidth(1024);
		this.getModel().setHeight(450);
		this.doRender();
		return this.getModel().getContent();
	}

}
