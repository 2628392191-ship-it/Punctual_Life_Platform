package com.sky.controller.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Api("员工管理")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation("员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation("员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }


    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
   @ApiOperation("添加员工")
    @PostMapping
    public Result add(@RequestBody EmployeeDTO employeeDTO){
        employeeService.add(employeeDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @ApiOperation("员工分页")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
          log.info("员工分页查询，参数：{}", employeePageQueryDTO);
          PageResult pageResult = employeeService.page(employeePageQueryDTO);
          return Result.success(pageResult);
    }

    /**
     * 启用/禁用员工账号
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("员工状态禁用/启用")
    @PostMapping("/status/{status}")
   public Result updateStatus(@PathVariable Integer status, Long id){
        log.info("员工状态禁用/启用：{}", status);
         employeeService.updateStatus(status, id);
         return Result.success();
    }

    @ApiOperation("根据ID查询员工信息")
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("员工信息查询：{}", id);
        Employee byId = employeeService.findById(id);
        if(byId == null) return null;
        byId.setPassword("*******");
        return  Result.success(byId);
    }


    @ApiOperation("员工信息修改")
    @PutMapping
    public Result updateEmployee(@RequestBody EmployeeDTO employeeDTO){
        log.info("员工信息修改：{}", employeeDTO);
         employeeService.updateEmployee(employeeDTO);
        return  Result.success();
    }



}
