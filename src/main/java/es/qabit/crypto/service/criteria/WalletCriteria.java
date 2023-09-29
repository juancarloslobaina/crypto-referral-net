package es.qabit.crypto.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link es.qabit.crypto.domain.Wallet} entity. This class is used
 * in {@link es.qabit.crypto.web.rest.WalletResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /wallets?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WalletCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter address;

    private BigDecimalFilter amount;

    private LongFilter userId;

    private LongFilter crytoId;

    private Boolean distinct;

    public WalletCriteria() {}

    public WalletCriteria(WalletCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.address = other.address == null ? null : other.address.copy();
        this.amount = other.amount == null ? null : other.amount.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.crytoId = other.crytoId == null ? null : other.crytoId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public WalletCriteria copy() {
        return new WalletCriteria(this);
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

    public StringFilter getAddress() {
        return address;
    }

    public StringFilter address() {
        if (address == null) {
            address = new StringFilter();
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public BigDecimalFilter getAmount() {
        return amount;
    }

    public BigDecimalFilter amount() {
        if (amount == null) {
            amount = new BigDecimalFilter();
        }
        return amount;
    }

    public void setAmount(BigDecimalFilter amount) {
        this.amount = amount;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getCrytoId() {
        return crytoId;
    }

    public LongFilter crytoId() {
        if (crytoId == null) {
            crytoId = new LongFilter();
        }
        return crytoId;
    }

    public void setCrytoId(LongFilter crytoId) {
        this.crytoId = crytoId;
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
        final WalletCriteria that = (WalletCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(address, that.address) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(crytoId, that.crytoId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, amount, userId, crytoId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WalletCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (address != null ? "address=" + address + ", " : "") +
            (amount != null ? "amount=" + amount + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (crytoId != null ? "crytoId=" + crytoId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
