
import com.example.backend.entity.Payment;
import com.example.backend.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void shouldFindLatestPaymentByPaymentId() {

        Optional<Payment> latestPayment =
                paymentRepository.findTopByOrderByPaymentIdDesc();

        assertTrue(latestPayment.isPresent(), "Payment should exist");

        Payment payment = latestPayment.get();

        assertNotNull(payment);
        assertNotNull(payment.getPaymentId());
    }
}