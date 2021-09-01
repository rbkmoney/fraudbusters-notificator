package com.rbkmoney.fraudbusters.notificator.resource;

import com.rbkmoney.fraudbusters.notificator.TestObjectsFactory;
import com.rbkmoney.fraudbusters.notificator.config.PostgresqlSpringBootITest;
import com.rbkmoney.fraudbusters.notificator.dao.domain.enums.ReportStatus;
import com.rbkmoney.fraudbusters.notificator.dao.domain.tables.records.ReportRecord;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static com.rbkmoney.fraudbusters.notificator.dao.domain.Tables.REPORT;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@PostgresqlSpringBootITest
class ReportResourceImplTest {

    @Autowired
    private WebApplicationContext context;

    protected MockMvc mockMvc;

    @Autowired
    private DSLContext dslContext;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    void findReportsByStatusAndFromTime() throws Exception {
        ReportRecord report1 = TestObjectsFactory.testReportRecord();
        report1.setCreatedAt(LocalDateTime.now().minusDays(1));
        ReportRecord report2 = TestObjectsFactory.testReportRecord();
        report2.setCreatedAt(LocalDateTime.now().plusDays(1));
        ReportRecord report3 = TestObjectsFactory.testReportRecord();
        report3.setStatus(ReportStatus.failed);
        dslContext.insertInto(REPORT)
                .set(report1)
                .newRecord()
                .set(report2)
                .newRecord()
                .set(report3)
                .execute();

        mockMvc.perform(MockMvcRequestBuilders.get("/reports")
                .param("status", "send")
                .param("from", LocalDateTime.now().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].result", is(report1.getResult())))
                .andExpect(status().isOk());
    }
}