package com.springboot.staking.quartz.jobs;

import com.springboot.staking.data.dao.StakingTxDao;
import com.springboot.staking.service.worker.StakingStateMachine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StakingJob implements Job {

    final private StakingTxDao stakingTxDao;
    final private StakingStateMachine stakingStateMachine;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("tick!");
        stakingTxDao.pickAnyReady().ifPresent(this::processOne);
    }

    private void processOne(Long id) {
        try {
            stakingStateMachine.processTransaction(id);
        } catch (Exception e) {
            log.error("Failed to process transaction {}: {}", id, e.getMessage(), e);
        }
    }
}