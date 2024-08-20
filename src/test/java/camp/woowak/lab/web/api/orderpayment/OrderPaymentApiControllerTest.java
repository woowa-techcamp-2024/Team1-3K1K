package camp.woowak.lab.web.api.orderpayment;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import camp.woowak.lab.payment.service.OrderPaymentSettlementService;

@WebMvcTest(OrderPaymentApiController.class)
@MockBean(JpaMetamodelMappingContext.class)
class OrderPaymentApiControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OrderPaymentSettlementService orderPaymentSettlementService;

	@Test
	@DisplayName("정산 요청을 성공적으로 처리하면 올바른 응답을 반환한다.")
	void settlement_ShouldReturnSuccessResponse() throws Exception {
		// Given
		doNothing().when(orderPaymentSettlementService).settleToAllVendors();

		// When
		ResultActions resultActions = mockMvc.perform(post("/orderPayments/settlement")
			.contentType("application/json"));

		// Then
		resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.data.resultMessage").value("모든 정산을 완료하였습니다."));

		verify(orderPaymentSettlementService, times(1)).settleToAllVendors();
	}
}