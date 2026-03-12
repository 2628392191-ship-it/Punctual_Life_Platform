package com.sky.employee.controller;


import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.employee.util.JwtProperties;
import com.sky.employee.util.JwtUtil;
import com.sky.entity.Employee;


import com.sky.employee.service.EmployeeService;


import com.sky.gateway.utils.common.constant.JwtClaimsConstant;
import com.sky.gateway.utils.common.result.PageResult;
import com.sky.gateway.utils.common.result.Result;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/employee")
@Api(tags="管理端-员工管理")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Resource
    private JwtProperties jwtProperties;

    @ApiOperation("员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);
        Employee employee = employeeService.login(employeeLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        //TODO:生成token由网关实现
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

    @ApiOperation("员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @ApiOperation("添加员工")
    @PostMapping
    public Result add(@RequestBody EmployeeDTO employeeDTO){
        employeeService.add(employeeDTO);
        return Result.success();
    }

    @ApiOperation("员工分页")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询，参数：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.page(employeePageQueryDTO);
        return Result.success(pageResult);
    }

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
        return Result.success(byId);
    }

    @ApiOperation("员工信息修改")
    @PutMapping
    public Result updateEmployee(@RequestBody EmployeeDTO employeeDTO){
        log.info("员工信息修改：{}", employeeDTO);
        employeeService.updateEmployee(employeeDTO);
        return Result.success();
    }

    @ApiOperation("修改密码")
    @PutMapping("/editPassword")
    public Result updatePassword(@RequestBody PasswordEditDTO passwordEditDTO){
        log.info("修改密码：{}",passwordEditDTO);
        employeeService.updatePassword(passwordEditDTO);
        return Result.success();
    }
}
