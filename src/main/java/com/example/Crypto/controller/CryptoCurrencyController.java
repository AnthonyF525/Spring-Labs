package com.example.Crypto.controller;

import com.example.Crypto.domain.CryptoCurrency;
import com.example.Crypto.service.CryptoCurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/crypto")
@CrossOrigin(origins = "*") // Allow frontend access
public class CryptoCurrencyController {
    
    private final CryptoCurrencyService cryptoService;
    
    @Autowired
    public CryptoCurrencyController(CryptoCurrencyService cryptoService) {
        this.cryptoService = cryptoService;
    }
    
    // GET /api/crypto - Get all cryptocurrencies
    @GetMapping
    public ResponseEntity<List<CryptoCurrency>> getAllCryptocurrencies() {
        List<CryptoCurrency> cryptos = cryptoService.getAllCryptoCurrencies();
        
        if (cryptos.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        
        return ResponseEntity.ok(cryptos); // 200 OK with data
    }
    
    // GET /api/crypto/{coinId} - Get specific cryptocurrency by coinId
    @GetMapping("/{coinId}")
    public ResponseEntity<CryptoCurrency> getCryptocurrencyByCoinId(@PathVariable String coinId) {
        Optional<CryptoCurrency> crypto = cryptoService.getCryptocurrencyByCoinId(coinId);
        
        if (crypto.isPresent()) {
            return ResponseEntity.ok(crypto.get()); // 200 OK
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
    
    // GET /api/crypto/gainers - Get top gainers (positive 24h change)
    @GetMapping("/gainers")
    public ResponseEntity<List<CryptoCurrency>> getTopGainers() {
        List<CryptoCurrency> gainers = cryptoService.getTopGainers();
        
        if (gainers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(gainers);
    }
    
    // GET /api/crypto/losers - Get top losers (negative 24h change)
    @GetMapping("/losers")
    public ResponseEntity<List<CryptoCurrency>> getTopLosers() {
        List<CryptoCurrency> losers = cryptoService.getTopLosers();
        
        if (losers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(losers);
    }
    
    // GET /api/crypto/sorted - Get cryptocurrencies sorted by price (highest first)
    @GetMapping("/sorted")
    public ResponseEntity<List<CryptoCurrency>> getCryptocurrenciesSortedByPrice() {
        List<CryptoCurrency> sortedCryptos = cryptoService.getCryptoCurrenciesByPriceDesc();
        
        if (sortedCryptos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(sortedCryptos);
    }
    
    // GET /api/crypto/stats - Get basic statistics
    @GetMapping("/stats")
    public ResponseEntity<CryptoStats> getCryptoStats() {
        List<CryptoCurrency> allCryptos = cryptoService.getAllCryptoCurrencies();
        
        if (allCryptos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        // Calculate basic stats
        int totalCryptos = allCryptos.size();
        long gainersCount = allCryptos.stream()
                .filter(crypto -> crypto.getChangePercent24h().compareTo(java.math.BigDecimal.ZERO) > 0)
                .count();
        long losersCount = allCryptos.stream()
                .filter(crypto -> crypto.getChangePercent24h().compareTo(java.math.BigDecimal.ZERO) < 0)
                .count();
        
        CryptoStats stats = new CryptoStats(totalCryptos, gainersCount, losersCount);
        return ResponseEntity.ok(stats);
    }
    
    // Inner class for statistics response
    public static class CryptoStats {
        private int totalCryptocurrencies;
        private long gainers;
        private long losers;
        
        public CryptoStats(int totalCryptocurrencies, long gainers, long losers) {
            this.totalCryptocurrencies = totalCryptocurrencies;
            this.gainers = gainers;
            this.losers = losers;
        }
        
        // Getters
        public int getTotalCryptocurrencies() { return totalCryptocurrencies; }
        public long getGainers() { return gainers; }
        public long getLosers() { return losers; }
        
        // Setters
        public void setTotalCryptocurrencies(int totalCryptocurrencies) { this.totalCryptocurrencies = totalCryptocurrencies; }
        public void setGainers(long gainers) { this.gainers = gainers; }
        public void setLosers(long losers) { this.losers = losers; }
    }
}
