package test;

import crowd.entity.Admin;
import crowd.entity.Role;
import org.fall.mapper.AdminMapper;
import org.fall.mapper.RoleMapper;
import org.fall.service.api.AdminService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-persist-mybatis.xml","classpath:spring-persist-tx.xml"})
public class CrowdTest {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private AdminService adminService;

    @Test
    public void testConnect() throws SQLException {
        Connection connection = dataSource.getConnection();
        System.out.println(connection);
    }

    @Test
    public void testInsert(){
            Admin admin = new Admin(null,"Tom","123","汤姆","tom@qq.com",null);
            int count = adminMapper.insert(admin);
            System.out.println(count);
    }

    @Test
    public void testInsertPatchAdmin(){
        for (int i = 0; i < 98; i++) {
        adminMapper.insert(new Admin(null,"loginAcct"+i,"123"+i,"userName"+i,"email@qq.com",null));
        }
    }

    @Test
    public void testInsertPatchRole(){
        for (int i = 0; i < 50; i++) {
            roleMapper.insert(new Role(null,"faker"+i));
        }
    }

    @Test
    public void logTest(){
        //获取Logger对象，这里传入的Class就是当前打印日志的类
        Logger logger = LoggerFactory.getLogger(CrowdTest.class);
        //等级 DEBUG < INFO < WARN < ERROR
        logger.debug("DEBUG!!!");
        logger.info("INFO!!!");
        logger.warn("WARN!!!");
        logger.error("ERROR!!!");
    }

    @Test
    public void txTest(){
        Admin admin = new Admin(null,"lily","321","丽丽","lily@qq.com",null);
        adminService.saveAdmin(admin);
    }
}
