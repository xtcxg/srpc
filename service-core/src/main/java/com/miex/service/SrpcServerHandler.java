package com.miex.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author liutz
 * @since 2022/3/9
 */
public class SrpcServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext context) throws Exception {

	}
}
