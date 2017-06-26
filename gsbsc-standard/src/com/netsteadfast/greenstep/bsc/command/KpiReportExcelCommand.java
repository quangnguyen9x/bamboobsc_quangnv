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
package com.netsteadfast.greenstep.bsc.command;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.netsteadfast.greenstep.BscConstants;
import com.netsteadfast.greenstep.base.AppContext;
import com.netsteadfast.greenstep.base.BaseChainCommandSupport;
import com.netsteadfast.greenstep.base.Constants;
import com.netsteadfast.greenstep.base.exception.ServiceException;
import com.netsteadfast.greenstep.base.model.DefaultResult;
import com.netsteadfast.greenstep.bsc.model.BscKpiCode;
import com.netsteadfast.greenstep.bsc.model.BscMeasureDataFrequency;
import com.netsteadfast.greenstep.bsc.model.BscStructTreeObj;
import com.netsteadfast.greenstep.bsc.service.IEmployeeService;
import com.netsteadfast.greenstep.bsc.service.IOrganizationService;
import com.netsteadfast.greenstep.bsc.util.AggregationMethodUtils;
import com.netsteadfast.greenstep.bsc.util.BscReportPropertyUtils;
import com.netsteadfast.greenstep.bsc.util.BscReportSupportUtils;
import com.netsteadfast.greenstep.model.UploadTypes;
import com.netsteadfast.greenstep.po.hbm.BbEmployee;
import com.netsteadfast.greenstep.po.hbm.BbOrganization;
import com.netsteadfast.greenstep.util.SimpleUtils;
import com.netsteadfast.greenstep.util.UploadSupportUtils;
import com.netsteadfast.greenstep.vo.DateRangeScoreVO;
import com.netsteadfast.greenstep.vo.EmployeeVO;
import com.netsteadfast.greenstep.vo.KpiVO;
import com.netsteadfast.greenstep.vo.ObjectiveVO;
import com.netsteadfast.greenstep.vo.OrganizationVO;
import com.netsteadfast.greenstep.vo.PerspectiveVO;
import com.netsteadfast.greenstep.vo.VisionVO;

public class KpiReportExcelCommand extends BaseChainCommandSupport implements Command {
	private IOrganizationService<OrganizationVO, BbOrganization, String> organizationService;
	private IEmployeeService<EmployeeVO, BbEmployee, String> employeeService;	
	
	@SuppressWarnings("unchecked")
	public KpiReportExcelCommand() {
		super();
		organizationService = (IOrganizationService<OrganizationVO, BbOrganization, String>)
				AppContext.getBean("bsc.service.OrganizationService");
		employeeService = (IEmployeeService<EmployeeVO, BbEmployee, String>)
				AppContext.getBean("bsc.service.EmployeeService");		
	}

	@Override
	public boolean execute(Context context) throws Exception {
		if (this.getResult(context)==null || !(this.getResult(context) instanceof BscStructTreeObj) ) {
			return false;
		}
		String uploadOid = this.createExcel(context);
		this.setResult(context, uploadOid);
		return false;
	}	
	
	private String createExcel(Context context) throws Exception {
		String visionOid = (String)context.get("visionOid");
		VisionVO vision = null;
		BscStructTreeObj treeObj = (BscStructTreeObj)this.getResult(context);
		for (VisionVO visionObj : treeObj.getVisions()) {
			if (visionObj.getOid().equals(visionOid)) {
				vision = visionObj;
			}
		}
		BscReportPropertyUtils.loadData();
		BscReportSupportUtils.loadExpression(); // 2015-04-18 add
		String fileName = SimpleUtils.getUUIDStr() + ".xlsx";
		String fileFullPath = Constants.getWorkTmpDir() + "/" + fileName;	
		int row = 0;
		if (context.get("pieCanvasToData") == null || context.get("barCanvasToData") == null) {
			row = 0;
		}
		XSSFWorkbook wb = new XSSFWorkbook();				
		XSSFSheet sh = wb.createSheet();
		
		row += this.createHead(wb, sh, row, vision);
		row = this.createMainBody(wb, sh, row, vision);
		
        FileOutputStream out = new FileOutputStream(fileFullPath);
        wb.write(out);
        out.close();
        wb = null;
        
        File file = new File(fileFullPath);
		String oid = UploadSupportUtils.create(
				Constants.getSystem(), UploadTypes.IS_TEMP, false, file, "kpi-report.xlsx");
		file = null;
		return oid;
	}
	
	private int createHead(XSSFWorkbook wb, XSSFSheet sh, int row, VisionVO vision) throws Exception {
		Row headRow = sh.createRow(row);
		int cell=0;
		XSSFCellStyle cellHeadStyle = wb.createCellStyle();
		XSSFFont cellHeadFont = wb.createFont();
		cellHeadFont.setBold(true);
		cellHeadStyle.setFont(cellHeadFont);
		cellHeadStyle.setBorderBottom(BorderStyle.THIN);
		cellHeadStyle.setBorderTop(BorderStyle.THIN);
		cellHeadStyle.setBorderRight(BorderStyle.THIN);
		cellHeadStyle.setBorderLeft(BorderStyle.THIN);
		cellHeadStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellHeadStyle.setAlignment(HorizontalAlignment.CENTER);
		cellHeadStyle.setWrapText(true);
		
		int cols = 8; 
		String arrHeadText[] = {"ID", "KPI", "Quan Điểm", "Đơn Vị Tính", "Trọng Số", "Mục Tiêu", "Điểm Đạt", "Điểm BSC"};
		for (int i=0; i<cols; i++) {
			Cell headCell1 = headRow.createCell(cell++);	
			headCell1.setCellValue(arrHeadText[i]);
			headCell1.setCellStyle(cellHeadStyle);
			if(i>2) sh.autoSizeColumn(i);
		}			
		return 1;
	}
	
	private int createMainBody(XSSFWorkbook wb, XSSFSheet sh, int row, VisionVO vision) throws Exception {
		XSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		float sumBSC = 0;
		for (int px=0; px<vision.getPerspectives().size(); px++) 
		{
			PerspectiveVO perspective = vision.getPerspectives().get(px);
			for (int ox=0; ox<perspective.getObjectives().size(); ox++) 
			{
				ObjectiveVO objective = perspective.getObjectives().get(ox);
				for (int kx=0; kx<objective.getKpis().size(); kx++) 
				{
					Row contentRow = sh.createRow(row++);
					KpiVO kpi = objective.getKpis().get(kx);				
					int cell = 0;
					int cols = 8;
					String arrContent[] = {kpi.getId(), kpi.getName(), perspective.getName(), kpi.getUnit(), kpi.getWeight().toString(), Float.toString(kpi.getTarget()), Float.toString(kpi.getScore()), Float.toString(kpi.getWeight().floatValue()*kpi.getScore()/100)};
					for (int i=0; i<cols; i++) 
					{
						Cell contentCell1 = contentRow.createCell(cell++);
						if(i==4)
						{
							Float content = Float.parseFloat(arrContent[i]);
							contentCell1.setCellValue(BscReportSupportUtils.parse2(content));
						}
						else if(i==6 || i==7)
						{
							Float content = Float.parseFloat(arrContent[i]);
							contentCell1.setCellValue(BscReportSupportUtils.parse(content));
						}
						else 
						{
							String content = arrContent[i];
							contentCell1.setCellValue(content);
						}
						contentCell1.setCellStyle(cellStyle);
						if(i<=2) sh.autoSizeColumn(i);
					}
					sumBSC = sumBSC + kpi.getWeight().floatValue()*kpi.getScore()/100;
				}
			}
		}
		Row contentRow = sh.createRow(row++);
		Cell contentCell2 = contentRow.createCell(6);
		contentCell2.setCellValue("Tổng điểm BSC");
		contentCell2.setCellStyle(cellStyle);
		sh.addMergedRegion( new CellRangeAddress((row-1), (row-1), 5, 6) );	
		Cell contentCell3 = contentRow.createCell(7);
		contentCell3.setCellValue(sumBSC);
		contentCell3.setCellStyle(cellStyle);
		return row++;
	}
}
