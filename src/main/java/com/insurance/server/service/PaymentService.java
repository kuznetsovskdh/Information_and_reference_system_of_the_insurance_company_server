package com.insurance.server.service;

import com.insurance.server.entity.Payment;
import com.insurance.server.exception.ResourceNotFoundException;
import com.insurance.server.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;


    public List<Payment> getAllPayments() {
        log.info("Получение всех платежей");
        return paymentRepository.findAll();
    }


    public Payment getPaymentById(Long id) {
        log.info("Поиск платежа с ID: {}", id);
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Платёж с ID " + id + " не найден"
                ));
    }


    public List<Payment> getPaymentsByPolicy(Long policyId) {
        log.info("Получение платежей по полису с ID: {}", policyId);
        return paymentRepository.findByRelatedPolicy_PolicyId(policyId);
    }


    @Transactional
    public Payment createPayment(Payment payment) {
        log.info("Создание нового платежа");

        if (payment.getPaymentStatus() == null) {
            payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
        }

        Payment saved = paymentRepository.save(payment);
        log.info("Платёж создан с ID: {}", saved.getPaymentId());
        return saved;
    }


    @Transactional
    public Payment updatePayment(Long id, Payment updatedPayment) {
        log.info("Обновление платежа с ID: {}", id);

        Payment payment = getPaymentById(id);

        if (updatedPayment.getPaymentStatus() != null) {
            payment.setPaymentStatus(updatedPayment.getPaymentStatus());
        }
        if (updatedPayment.getPaymentNotes() != null) {
            payment.setPaymentNotes(updatedPayment.getPaymentNotes());
        }

        return paymentRepository.save(payment);
    }


    @Transactional
    public void deletePayment(Long id) {
        log.info("Удаление платежа с ID: {}", id);

        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Платёж с ID " + id + " не найден");
        }
        paymentRepository.deleteById(id);
    }


    public BigDecimal calculateTotalPaymentsByPolicy(Long policyId) {
        log.info("Расчёт общей суммы платежей по полису с ID: {}", policyId);
        BigDecimal total = paymentRepository.calculateTotalPaymentsByPolicy(policyId);
        return total != null ? total : BigDecimal.ZERO;
    }


    public BigDecimal getTotalCompletedPayments() {
        BigDecimal total = paymentRepository.getTotalCompletedPaymentsAmount();
        return total != null ? total : BigDecimal.ZERO;
    }
}