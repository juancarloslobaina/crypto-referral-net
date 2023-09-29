package es.qabit.crypto.service.dto;

import es.qabit.crypto.domain.enumeration.TransactionStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link es.qabit.crypto.domain.Transaction} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionDTO implements Serializable {

    private Long id;

    private BigDecimal balance;

    private Instant date;

    private TransactionStatus status;

    private UserDTO userFrom;

    private UserDTO userTo;

    private CryptocurrencyDTO crypto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public UserDTO getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(UserDTO userFrom) {
        this.userFrom = userFrom;
    }

    public UserDTO getUserTo() {
        return userTo;
    }

    public void setUserTo(UserDTO userTo) {
        this.userTo = userTo;
    }

    public CryptocurrencyDTO getCrypto() {
        return crypto;
    }

    public void setCrypto(CryptocurrencyDTO crypto) {
        this.crypto = crypto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionDTO)) {
            return false;
        }

        TransactionDTO transactionDTO = (TransactionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionDTO{" +
            "id=" + getId() +
            ", balance=" + getBalance() +
            ", date='" + getDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", userFrom=" + getUserFrom() +
            ", userTo=" + getUserTo() +
            ", crypto=" + getCrypto() +
            "}";
    }
}
