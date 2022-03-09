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

	private SrpcException(Integer code,String msg) {
		super(msg);
		this.code = code;
		this.msg = msg;
	}

	public SrpcException(Enum e) {
		this(e.getCode(),e.getMsg());
	}

	public SrpcException(Enum e,String msg) {
		this(e.getCode(),msg);
	}

	public enum Enum{
		SYSTEM_ERROR(50001,"system error"),
		PARAM_ERROR(30001,"param verification failed")
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
