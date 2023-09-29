package es.qabit.crypto.service.criteria;

import es.qabit.crypto.domain.enumeration.TransactionStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link es.qabit.crypto.domain.Transaction} entity. This class is used
 * in {@link es.qabit.crypto.web.rest.TransactionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transactions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TransactionStatus
     */
    public static class TransactionStatusFilter extends Filter<TransactionStatus> {

        public TransactionStatusFilter() {}

        public TransactionStatusFilter(TransactionStatusFilter filter) {
            super(filter);
        }

        @Override
        public TransactionStatusFilter copy() {
            return new TransactionStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter balance;

    private InstantFilter date;

    private TransactionStatusFilter status;

    private LongFilter userFromId;

    private LongFilter userToId;

    private LongFilter cryptoId;

    private Boolean distinct;

    public TransactionCriteria() {}

    public TransactionCriteria(TransactionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.balance = other.balance == null ? null : other.balance.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.userFromId = other.userFromId == null ? null : other.userFromId.copy();
        this.userToId = other.userToId == null ? null : other.userToId.copy();
        this.cryptoId = other.cryptoId == null ? null : other.cryptoId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TransactionCriteria copy() {
        return new TransactionCriteria(this);
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

    public BigDecimalFilter getBalance() {
        return balance;
    }

    public BigDecimalFilter balance() {
        if (balance == null) {
            balance = new BigDecimalFilter();
        }
        return balance;
    }

    public void setBalance(BigDecimalFilter balance) {
        this.balance = balance;
    }

    public InstantFilter getDate() {
        return date;
    }

    public InstantFilter date() {
        if (date == null) {
            date = new InstantFilter();
        }
        return date;
    }

    public void setDate(InstantFilter date) {
        this.date = date;
    }

    public TransactionStatusFilter getStatus() {
        return status;
    }

    public TransactionStatusFilter status() {
        if (status == null) {
            status = new TransactionStatusFilter();
        }
        return status;
    }

    public void setStatus(TransactionStatusFilter status) {
        this.status = status;
    }

    public LongFilter getUserFromId() {
        return userFromId;
    }

    public LongFilter userFromId() {
        if (userFromId == null) {
            userFromId = new LongFilter();
        }
        return userFromId;
    }

    public void setUserFromId(LongFilter userFromId) {
        this.userFromId = userFromId;
    }

    public LongFilter getUserToId() {
        return userToId;
    }

    public LongFilter userToId() {
        if (userToId == null) {
            userToId = new LongFilter();
        }
        return userToId;
    }

    public void setUserToId(LongFilter userToId) {
        this.userToId = userToId;
    }

    public LongFilter getCryptoId() {
        return cryptoId;
    }

    public LongFilter cryptoId() {
        if (cryptoId == null) {
            cryptoId = new LongFilter();
        }
        return cryptoId;
    }

    public void setCryptoId(LongFilter cryptoId) {
        this.cryptoId = cryptoId;
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
        final TransactionCriteria that = (TransactionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(balance, that.balance) &&
            Objects.equals(date, that.date) &&
            Objects.equals(status, that.status) &&
            Objects.equals(userFromId, that.userFromId) &&
            Objects.equals(userToId, that.userToId) &&
            Objects.equals(cryptoId, that.cryptoId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance, date, status, userFromId, userToId, cryptoId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (balance != null ? "balance=" + balance + ", " : "") +
            (date != null ? "date=" + date + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (userFromId != null ? "userFromId=" + userFromId + ", " : "") +
            (userToId != null ? "userToId=" + userToId + ", " : "") +
            (cryptoId != null ? "cryptoId=" + cryptoId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
