package com.springboot.staking.common.constant;

import lombok.Getter;

@Getter
public enum Step {
  CREATE("트랜잭션 생성"),
  SIGN("트랜잭션 서명"),
  BROADCAST("트랜잭션 브로드캐스트"),
  CONFIRM("트랜잭션 확인");

  static {
    CREATE.next = SIGN;
    SIGN.next = BROADCAST;
    BROADCAST.next = CONFIRM;
    CONFIRM.next = CONFIRM;
  }

  private final String description;
  private Step next;

  Step(String description) {
    this.description = description;
  }

  public boolean hasNext() {
    return next != null;
  }

  public boolean isTerminal() {
    return next == null;
  }

  public boolean canTransitionTo(Step target) {
    return this.next == target;
  }

}
