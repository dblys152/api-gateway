package com.ys.infrastructure.jwt;

public class PayloadInfoStore {
    public static final ThreadLocal<PayloadInfo> THREAD_LOCAL = new ThreadLocal<>();
}
