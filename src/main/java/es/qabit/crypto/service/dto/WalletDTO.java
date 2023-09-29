package es.qabit.crypto.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link es.qabit.crypto.domain.Wallet} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WalletDTO implements Serializable {

    private Long id;

    private String address;

    private BigDecimal balance;

    private UserDTO user;

    private CryptocurrencyDTO cryto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public CryptocurrencyDTO getCryto() {
        return cryto;
    }

    public void setCryto(CryptocurrencyDTO cryto) {
        this.cryto = cryto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WalletDTO)) {
            return false;
        }

        WalletDTO walletDTO = (WalletDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, walletDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WalletDTO{" +
            "id=" + getId() +
            ", address='" + getAddress() + "'" +
            ", balance=" + getBalance() +
            ", user=" + getUser() +
            ", cryto=" + getCryto() +
            "}";
    }
}
