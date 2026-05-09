package com.sky.api.client;

import com.sky.entity.AddressBook;
import com.sky.entity.ShoppingCart;
import com.sky.entity.User;
import com.sky.gateway.utils.common.result.Result;
import com.sky.vo.UserReportVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@FeignClient("user-service")
public interface UserClient {

     @GetMapping("/user/user/{userId}")
     Result<User> getUserById(@PathVariable Long userId);

     @GetMapping("/user/addressBook/{id}")
     Result<AddressBook> getAddressBookById(@PathVariable Long id);

     @PostMapping("/user/user/statistics")
     Result<Integer> UserStatisticsTotal(@RequestBody Map<String, Object> map);

     @GetMapping("/user/user/userStatistics")
     Result<UserReportVO> userStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end);
}
