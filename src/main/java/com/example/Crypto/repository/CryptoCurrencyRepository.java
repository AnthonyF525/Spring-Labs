package com.example.Crypto.repository;

import com.example.Crypto.domain.CryptoCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CryptoCurrencyRepository extends JpaRepository<CryptoCurrency, Long> {
    //Method name becomes SQL query automatically
    Optional<CryptoCurrency> findByCoinId(String coinId);

    //Find all cryptos, sorted by price
    List<CryptoCurrency> findAllByOrderByPriceUsdDesc();

    //Find where prices is greater tha specified
    List<CryptoCurrency> findByPriceUsdGreaterThan(java.math.BigDecimal price);

    //Check if a crypto exists by coinId
    boolean existsByCoinId(String coinId);

    //Custom: When method names get to complex use @Query
    @Query("SELECT c FROM CryptoCurrency c WHERE c.changePercent24h > 0 ORDER BY c.changePercent24h DESC")
    List<CryptoCurrency> findGainersOrderedByChange();

    //Custom: Find losers (negative change)
    @Query("SELECT c FROM CryptoCurrency c WHERE c.changePercent24h < 0 ORDER BY c.changePercent24h ASC")
    List<CryptoCurrency> findLosersOrderedByChange();
    
}
