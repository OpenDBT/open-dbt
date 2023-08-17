package com.highgo.opendbt.common.utils;

/**
 * @Description: AtomicFloat原子类
 * @Title: AtomicFloat
 * @Package com.highgo.opendbt.common.utils
 * @Author: highgo
 * @Copyright 版权归HIGHGO企业所有
 * @CreateTime: 2023/8/14 16:09
 */
import java.util.concurrent.atomic.AtomicInteger;

/**
 * jdk没有AtomicFloat，写一个
 */
public class AtomicFloat extends Number {

  private AtomicInteger bits;

  public AtomicFloat() {
    this(0f);
  }

  public AtomicFloat(float initialValue) {
    bits = new AtomicInteger(Float.floatToIntBits(initialValue));
  }

  //叠加
  public final float addAndGet(float delta) {
    float expect;
    float update;
    do {
      expect = get();
      update = expect + delta;
    } while (!this.compareAndSet(expect, update));

    return update;
  }

  public final float getAndAdd(float delta) {
    float expect;
    float update;
    do {
      expect = get();
      update = expect + delta;
    } while (!this.compareAndSet(expect, update));

    return expect;
  }

  public final float getAndDecrement() {
    return getAndAdd(-1);
  }

  public final float decrementAndGet() {
    return addAndGet(-1);
  }

  public final float getAndIncrement() {
    return getAndAdd(1);
  }

  public final float incrementAndGet() {
    return addAndGet(1);
  }

  public final float getAndSet(float newValue) {
    float expect;
    do {
      expect = get();
    } while (!this.compareAndSet(expect, newValue));

    return expect;
  }

  public final boolean compareAndSet(float expect, float update) {
    return bits.compareAndSet(Float.floatToIntBits(expect), Float.floatToIntBits(update));
  }

  public final void set(float newValue) {
    bits.set(Float.floatToIntBits(newValue));
  }

  public final float get() {
    return Float.intBitsToFloat(bits.get());
  }

  @Override
  public float floatValue() {
    return get();
  }

  @Override
  public double doubleValue() {
    return (double) floatValue();
  }

  @Override
  public int intValue() {
    return (int) get();
  }

  @Override
  public long longValue() {
    return (long) get();
  }

  @Override
  public String toString() {
    return Float.toString(get());
  }
}
