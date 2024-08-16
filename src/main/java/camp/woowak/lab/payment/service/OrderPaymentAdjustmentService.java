package camp.woowak.lab.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import camp.woowak.lab.menu.repository.MenuRepository;
import camp.woowak.lab.payment.repository.OrderPaymentRepository;
import camp.woowak.lab.vendor.domain.Vendor;
import camp.woowak.lab.vendor.exception.NotFoundVendorException;
import camp.woowak.lab.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;

/**
 * 정산을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class OrderPaymentAdjustmentService {

	private final VendorRepository vendorRepository;
	private final MenuRepository menuRepository;
	private final OrderPaymentRepository orderPaymentRepository;

	/**
	 * @throws NotFoundVendorException vendorId에 해당하는 점주를 찾을 수 없을 떄
	 */
	@Transactional
	public void adjustment() {
		// 1. 모든 점주 조회
		List<Vendor> vendors = findAllVendors();
	}

	// 모든 점주 조회
	private List<Vendor> findAllVendors() {
		return vendorRepository.findAll();
	}

}
