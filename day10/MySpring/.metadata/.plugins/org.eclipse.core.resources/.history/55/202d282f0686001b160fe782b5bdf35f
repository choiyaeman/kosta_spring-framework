package control;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(loadOnStartup = 1, urlPatterns = {"/*"}) //loadOnStartup=1값을 주게되면 톰캣이 구동하자마자 서블릿 객체가 미리 만들어진다. 요청하지도 않아도 서블릿 객체를 미리 만든다.
public class BoardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String contextPath = request.getContextPath(); // ex: http://localhost:8888/boardbackController/list라면
		                          //  /boardbackController를 반환
		String requestURI = request.getRequestURI(); //  /boardbackController/list를 반환
		
		String subpath = requestURI.substring(contextPath.length(), requestURI.length());
		System.out.println("BoardServlet이 요청됨 subpath=" + subpath);
		
		//CORS정책
		response.setHeader("Access-Control-Allow-Origin", "*");
		//응답형식
		response.setContentType("application/json;charset=UTF-8");
		//응답출력스트림 얻기
		PrintWriter out = response.getWriter();
		
//		request.setCharacterEncoding("utf-8");		
		//----------------------------------------
		Properties controllerEnv = new Properties();
		String propertiesRealPath = 
				getServletContext().getRealPath("controller.properties");
		controllerEnv.load(new FileInputStream(propertiesRealPath));
		String className = controllerEnv.getProperty(subpath);
		try {
			Class clazz = Class.forName(className); //클래스 이름을 찾아 JVM위쪽으로 올린다 -> runtime dynamic load			
			Object obj = clazz.newInstance(); //객체생성하기			
			Method m = clazz.getDeclaredMethod("execute", HttpServletRequest.class, HttpServletResponse.class);
			Object result = m.invoke(obj, request, response); //execute메서드 호출하기
			out.print(result);
		} catch (ClassNotFoundException e) { //Class.forName
			e.printStackTrace();
		} catch (InstantiationException e) { //clazz.newInstance
			e.printStackTrace();
		} catch (IllegalAccessException e) { //clazz.newInstance
			e.printStackTrace();
		} catch (NoSuchMethodException e) {  //clazz.getDeclaredMethod
			e.printStackTrace();
		} catch (SecurityException e) { //clazz.getDeclaredMethod
			e.printStackTrace();
		} catch (IllegalArgumentException e) { //m.invoke
			e.printStackTrace();
		} catch (InvocationTargetException e) { //m.invoke
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		PrintWriter out = response.getWriter();
//		String controllerPropertiesPath = 
//				getServletContext().getRealPath("controller.properties");
//		Properties env = new Properties();
//		env.load(new FileInputStream(controllerPropertiesPath));
//		
//		String controllerClassName = env.getProperty(subpath);
//		try {
//			Class clazz = Class.forName(controllerClassName);
//			Object obj = clazz.newInstance();
//			Method executeMethod = clazz.getDeclaredMethod("execute", HttpServletRequest.class, HttpServletResponse.class);
//			Object result = executeMethod.invoke(obj, request, response);
//			out.print(result);
//		} catch (ClassNotFoundException e1) { //Class.forName()
//			e1.printStackTrace();
//		} catch (InstantiationException e) { //clazz.newInstance()
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {//clazz.newInstance(), invoke()
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) { //getDeclaredMethod()
//			e.printStackTrace();
//		} catch (SecurityException e) { //getDeclaredMethod()
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) { //invoke()
//			e.printStackTrace();
//		} catch (InvocationTargetException e) { //invoke()
//			e.printStackTrace();
//		}
	}
}