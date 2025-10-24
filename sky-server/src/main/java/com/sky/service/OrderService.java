package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.*;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    PageResult UserPageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    void cancel(OrdersCancelDTO ordersCancelDTO);

    void complete(Long id);

    void reject(OrdersRejectionDTO rejectionDTO);

    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    OrderVO details(Long id);

    void delivery(Long id);

    OrderStatisticsVO statistics();

    void PaySuccess(OrdersPaymentDTO ordersPaymentDTO);

    void reminder(Long id);

    void UserCancel(Long id);

    void repetition(Long id);

    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);

    OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);

    SalesTop10ReportVO top10(LocalDate begin, LocalDate end);
}
