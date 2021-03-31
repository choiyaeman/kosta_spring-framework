package com.my.service;

import org.springframework.stereotype.Service;

@Service
public class Target { //핵심로직을 담고 있는 클래스
	public void a() { //JoinPoint
		System.out.println("a()메서드호출됨");
	}
	public void a(int num) { //joinpoint
		System.out.println("a(num)메서드호출됨");
	}
	public int b(int num) { //joinpoint
		System.out.println("b()메서드호출됨");
		return num*10;
	}
	public void c() { //joinpoint
		System.out.println("c()메서드호출됨");
	}
	public void d() { //joinpoint
		System.out.println("d()메서드호출됨");
	}
}
