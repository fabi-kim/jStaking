package com.springboot.staking.service.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.springboot.staking.common.constant.Symbol;
import com.springboot.staking.data.dto.request.BroadcastRequest;
import com.springboot.staking.data.dto.request.SignRequest;
import com.springboot.staking.data.dto.request.StakingRequest;
import com.springboot.staking.data.dto.response.BroadcastResponse;
import com.springboot.staking.data.dto.response.DelegateTxResponse;
import com.springboot.staking.data.dto.response.TransactionResponse;
import com.springboot.staking.fixture.StakingFixtures;
import com.springboot.staking.service.NodeService;
import com.springboot.staking.service.NodeServiceFactory;
import com.springboot.staking.service.StakingService;
import com.springboot.staking.service.StakingServiceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DelegateTxWorkflowTest {

  //
  Symbol symbol = Symbol.TIA;

  @Mock
  StakingServiceFactory stakingFactory;
  @Mock
  NodeServiceFactory nodeFactory;
  @Mock
  private StakingService stakingService;
  @Mock
  private NodeService nodeService;

  @InjectMocks
  private DelegateTxWorkflow workflow;

  @BeforeEach
  void setUp() {
    // 팩토리 스텁: 해당 심볼이면 우리가 만든 mock 서비스 반환
    when(stakingFactory.getServiceBySymbol(symbol)).thenReturn(stakingService);
    when(nodeFactory.getServiceBySymbol(symbol)).thenReturn(nodeService);
  }

  @Test
  @DisplayName("run: 정상적인 순서로 동작하는지 확인")
  void run_success() {
    String unsignedTx = "RAW_TX";
    StakingRequest stakingRequest = StakingFixtures.stakingRequest();
    DelegateTxResponse delegateTxResponse = StakingFixtures.delegateTxResponseWithUnsignedTx(unsignedTx);
    String signed = "0x";
    BroadcastResponse broadcastResponse = StakingFixtures.broadcastResponse();
    TransactionResponse transactionResponse = StakingFixtures.transactionResponse();

    when(stakingService.createDelegateTx(stakingRequest)).thenReturn(delegateTxResponse);
    when(nodeService.sign(any(SignRequest.class))).thenReturn(signed);
    when(nodeService.broadcast(any(BroadcastRequest.class))).thenReturn(broadcastResponse);
    when(nodeService.getTx(any(String.class))).thenReturn(transactionResponse);

    var result = workflow.run(Symbol.TIA, stakingRequest);
    assertEquals(transactionResponse, result);

    //순서/인자 검증
    InOrder io = inOrder(stakingService, nodeService);
    io.verify(stakingService).createDelegateTx(stakingRequest);
    io.verify(nodeService).sign(any(SignRequest.class));
    io.verify(nodeService).broadcast(any(BroadcastRequest.class));
    io.verify(nodeService).getTx(any(String.class));

    var captor = ArgumentCaptor.forClass(SignRequest.class);
    verify(nodeService).sign(captor.capture());
    assertEquals(unsignedTx, captor.getValue().unsignedTx());
  }
  //도중에 메소드 실패하면 나머지 호출 안되는지 확인

}
