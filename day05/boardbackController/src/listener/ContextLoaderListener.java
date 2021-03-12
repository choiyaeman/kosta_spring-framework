package listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.my.service.RepBoardService;

@WebListener
public class ContextLoaderListener implements ServletContextListener {
	
	//ServletContext객체생성시 자동호출되는 메서드
    public void contextInitialized(ServletContextEvent sce)  { 
        RepBoardService service; //static변수가 포함되어있는 클래스가 jvm로드 되자마자 자동 초기화 되기 때문.
    	
    	
    }
    //ServletContext객체소멸시 자동호출되는 메서드
    public void contextDestroyed(ServletContextEvent sce)  { 
         
    }


	
}
