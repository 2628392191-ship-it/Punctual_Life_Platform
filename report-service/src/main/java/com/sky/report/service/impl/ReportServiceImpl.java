package com.sky.report.service.impl;

import com.sky.api.client.OrderClient;
import com.sky.api.client.UserClient;
import com.sky.gateway.utils.common.result.Result;
import com.sky.report.service.ReportService;
import com.sky.report.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Service
@Slf4j
public class ReportServiceImpl implements ReportService  {

    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private HttpServletResponse response;
    @Resource
    private OrderClient orderClient;
    @Resource
    private UserClient userClient;


    @Override
    public void export()  {
        InputStream is=null;
        XSSFWorkbook ws=null;
         try {
           //处理近30天的数据
           //这里使用当前的时间是因为此后是不知道是否有订单的
           //withNano(0)将秒以后的单位表示的精度设置为0
           LocalDateTime end = LocalDateTime.now().withNano(0);
           //开始的时间设置为00:00:00d的目的是让这之前的数据也能被查询到
           LocalDateTime begin = LocalDateTime.of(end.minusDays(30).toLocalDate(), LocalTime.MIN);
           //查询到30天的具体数据
           BusinessDataVO vo = workspaceService.getBusinessData(begin, end);

           //通过类加载器的特性把模板文件放在类路径下并进行读取
           is= ReportServiceImpl.class.getClassLoader().getResourceAsStream("运营数据报表模板.xlsx");
           //设置读入的模板创建一个Excel对象使用
           ws = new XSSFWorkbook(is);

           //拿到第一个Sheet
           XSSFSheet sheetAt = ws.getSheetAt(0);

           //设置运营数据报表起止时间
           XSSFRow row3 = sheetAt.getRow(1);
           row3.getCell(1).setCellValue(begin + "--" + end);

           //设置总营业额
           XSSFRow row = sheetAt.getRow(3);
           row.getCell(2).setCellValue(vo.getTurnover());

           //设置订单总完成率
           row.getCell(4).setCellValue(vo.getOrderCompletionRate());
           //设置新增用户的总数
           row.getCell(6).setCellValue(vo.getNewUsers());


           XSSFRow row1 = sheetAt.getRow(4);
           //设置有效订单的总数
           row1.getCell(2).setCellValue(vo.getValidOrderCount());
           //设置平均客单价
           row1.getCell(4).setCellValue(vo.getUnitPrice());


           for (int i = 0; i < 30; i++) {
               //要让时间回溯，便设置一个LocalDate-i
               LocalDate currentDate = LocalDate.now().minusDays(i);
               LocalDateTime beginTime = LocalDateTime.of(currentDate, LocalTime.MIN);
               LocalDateTime endTime = LocalDateTime.of(currentDate, LocalTime.MAX);
               //获取每天的运营数据
               BusinessDataVO vo1 = workspaceService.getBusinessData(beginTime, endTime);

               //对每一天的运营数据进行填充
               XSSFRow row2 = sheetAt.getRow(i + 7);
               row2.getCell(1).setCellValue(currentDate.toString());
               row2.getCell(2).setCellValue(vo1.getTurnover() != null ? vo1.getTurnover() : 0.0);
               row2.getCell(3).setCellValue(vo1.getValidOrderCount() != null ? vo1.getValidOrderCount() : 0);
               row2.getCell(4).setCellValue(vo1.getOrderCompletionRate() != null ? vo1.getOrderCompletionRate() : 0.0);
               row2.getCell(5).setCellValue(vo1.getUnitPrice() != null ? vo1.getUnitPrice() : 0.0);
               row2.getCell(6).setCellValue(vo1.getNewUsers() != null ? vo1.getNewUsers() : 0);
            }
       } catch (Exception e) {
           e.printStackTrace();
       }
         finally{
             try {
                 //使用HttpServletResponse的输出流进行数据输出
                 ws.write(response.getOutputStream());
                 ws.close();
                 is.close();
             }catch (IOException e) {
                 throw new RuntimeException(e);
             }
         }

    }

    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        return orderClient.reportTurnoverStatistics(begin, end).getData();
    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
       return userClient.userStatistics(begin, end).getData();
    }

    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        return orderClient.orderStatistics(begin, end).getData();
    }

    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        return orderClient.top10(begin, end).getData();
    }

}
