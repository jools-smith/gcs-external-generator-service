package com.revenera.gcs.logging;

import com.revenera.gcs.utils.Frame;

import java.time.Instant;

class Context {
  final Instant time = Instant.now();
  final Level level;
  final Frame frame;

  Context(final Level level, final short depth) {
    this.level = level;
    this.frame = new Frame(depth);
  }
  
  public Level getLevel() {
    //
    return level;
  }

  //UTC
  public String getTimeUtc() {
    return time.toString()
        .replace("T", " ")
        .replace("Z", "");
  }

  public String getClassName() {
    //
    return this.frame.getClassName();
  }

  public String getMethodName () {
    //
    return this.frame.getMethodName();
  }

  int getLineNumber(){
    //
    return this.frame.getLineNumber();
  }
}
