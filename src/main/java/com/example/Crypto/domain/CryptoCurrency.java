package com.example.Crypto.domain;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "crypto_currencies")
public class CryptoCurrency {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coin_id", nullable = false, unique = true, length = 50)
    private String coinId;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "price_usd", precision = 19, scale = 8)
    private BigDecimal priceUsd;
    
    @Column(name = "change_percent_24h", precision = 10, scale = 4)
    private BigDecimal changePercent24h;

    @Column(name = "volume_24h", precision = 19, scale = 2)
    private BigDecimal volume24h;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    protected CryptoCurrency() {
        //
    }

    public CryptoCurrency(String coinId, String displayName,
                         BigDecimal priceUsd, BigDecimal changePercent24h,
                         BigDecimal volume24h) {
        this.coinId = coinId;
        this.displayName = displayName;
        this.priceUsd = priceUsd;
        this.changePercent24h = changePercent24h;
        this.volume24h = volume24h;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public BigDecimal getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(BigDecimal priceUsd) {
        this.priceUsd = priceUsd;
    }

    public BigDecimal getChangePercent24h() {
        return changePercent24h;
    }

    public void setChangePercent24h(BigDecimal changePercent24h) {
        this.changePercent24h = changePercent24h;
    }

    public BigDecimal getVolume24h() {
        return volume24h;
    }

    public void setVolume24h(BigDecimal volume24h) {
        this.volume24h = volume24h;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "CryptoCurrency{" +
                "id=" + id +
                ", coinId='" + coinId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", priceUsd=" + priceUsd +
                ", changePercent24h=" + changePercent24h +
                ", volume24h=" + volume24h +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
