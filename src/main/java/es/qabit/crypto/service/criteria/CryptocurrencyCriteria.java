package es.qabit.crypto.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link es.qabit.crypto.domain.Cryptocurrency} entity. This class is used
 * in {@link es.qabit.crypto.web.rest.CryptocurrencyResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cryptocurrencies?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CryptocurrencyCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter symbol;

    private BigDecimalFilter exchangeRate;

    private Boolean distinct;

    public CryptocurrencyCriteria() {}

    public CryptocurrencyCriteria(CryptocurrencyCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.symbol = other.symbol == null ? null : other.symbol.copy();
        this.exchangeRate = other.exchangeRate == null ? null : other.exchangeRate.copy();
        this.distinct = other.distinct;
    }

    @Override
    public CryptocurrencyCriteria copy() {
        return new CryptocurrencyCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getSymbol() {
        return symbol;
    }

    public StringFilter symbol() {
        if (symbol == null) {
            symbol = new StringFilter();
        }
        return symbol;
    }

    public void setSymbol(StringFilter symbol) {
        this.symbol = symbol;
    }

    public BigDecimalFilter getExchangeRate() {
        return exchangeRate;
    }

    public BigDecimalFilter exchangeRate() {
        if (exchangeRate == null) {
            exchangeRate = new BigDecimalFilter();
        }
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimalFilter exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CryptocurrencyCriteria that = (CryptocurrencyCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(symbol, that.symbol) &&
            Objects.equals(exchangeRate, that.exchangeRate) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, symbol, exchangeRate, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CryptocurrencyCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (symbol != null ? "symbol=" + symbol + ", " : "") +
            (exchangeRate != null ? "exchangeRate=" + exchangeRate + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
