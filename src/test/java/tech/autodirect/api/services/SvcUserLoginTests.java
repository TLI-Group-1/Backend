package tech.autodirect.api.services;

import org.junit.jupiter.api.Test;
import tech.autodirect.api.database.TableUsers;
import tech.autodirect.api.interfaces.TableUsersInterface;
import tech.autodirect.api.upstream.BankApi;

import java.sql.SQLException;
import java.util.Map;

public class SvcUserLoginTests {
    @Test void svcUserLoginTest(){
        try{
            SvcUserLogin loginClass = new SvcUserLogin(new TableUsers("autodirect"), new BankApi());
            String userId = new String("username_member824");
            Map<String, Object> result = (username);

        }
        catch(SQLException e) {
            e.printStackTrace();
            assert false;
        }
    }
}
