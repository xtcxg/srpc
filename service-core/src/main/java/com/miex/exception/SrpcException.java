package com.miex.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liutz
 * @since 2022/3/8
 */
@Slf4j
public class SrpcException extends RuntimeException{
	@Getter
	private Integer code;
	@Getter
	private String msg;

	public SrpcException(){
		super(Enum.SYSTEM_ERROR.getMsg());
		this.code = Enum.SYSTEM_ERROR.getCode();
		this.msg = Enum.SYSTEM_ERROR.getMsg();
	}

	public SrpcException(Integer code,String msg) {
		super(msg);
		this.code = code;
		this.msg = msg;
	}

	private SrpcException(Integer code,String msg, Exception e) {
		super(msg, e);
		this.code = code;
		this.msg = msg;
	}

	public SrpcException(Enum em, Exception e) {
		this(em.getCode(),em.getMsg(),e);
	}

	public SrpcException(Enum em, String msg, Exception e) {
		this(em.getCode(),msg,e);
	}

	public SrpcException(Enum e) {
		this(e.getCode(),e.getMsg());
	}

	public SrpcException(Enum e,String msg) {
		this(e.getCode(),msg);
	}

	public enum Enum{
		PARAM_ERROR(30001,"param verification failed"),

		//
		EXPORTER_ERROR(40001,"exporter error"),
		INVOKER_ERROR(40002,"invoker error"),
		RESOURCE_NOT_FOUND(40004, "resource not found"),
		CLIENT_ERROR(40005, "client error"),
		SERVER_ERROR(40006, "server error"),
		TRANSFER_ERROR(40007, "transfer error"),
		EXCHANGE_ERROR(40008, "exchange error"),


		SYSTEM_ERROR(50001,"system error"),
		SERIALIZABLE_ERROR(50002,"serializable error"),
		REGISTRY_CONNECT_FAIL(50003, "registry connect fail"),
		REGISTRY_LOCK_FAIL(50004, "registry lock fail"),
		SEND_REQUEST_ERROR(50005,"send request error"),
		NO_SERVER_AVAILABLE(50006, "no server available"),
		;
		@Getter
		private final Integer code;
		@Getter
		private final String msg;

		Enum(Integer code, String msg) {
			this.code = code;
			this.msg = msg;
		}
	}
}
