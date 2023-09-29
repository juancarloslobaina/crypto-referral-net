package es.qabit.crypto.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link es.qabit.crypto.domain.Cryptocurrency} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CryptocurrencyDTO implements Serializable {

    private Long id;

    private String name;

    private String symbol;

    private BigDecimal exchangeRate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CryptocurrencyDTO)) {
            return false;
        }

        CryptocurrencyDTO cryptocurrencyDTO = (CryptocurrencyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cryptocurrencyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CryptocurrencyDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", symbol='" + getSymbol() + "'" +
            ", exchangeRate=" + getExchangeRate() +
            "}";
    }
}
