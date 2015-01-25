package com.polluxlab.banglamusic.util;

/**
 * Created by samiron on 1/25/2015.
 */
public interface StorageDataProvider<T> {
    public T getData();
    public boolean validate(T t);
}
