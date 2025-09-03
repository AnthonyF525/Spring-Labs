package com.example.Crypto.service;

import com.example.Crypto.domain.CryptoCurrency;
import com.example.Crypto.repository.CryptoCurrencyRepository;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CryptoCurrencyService {

    private final CryptoCurrencyRepository repository;
    private final WebClient webClient;

    // Spring can automagically provide constructor Injector
    @Autowired
    public CryptoCurrencyService(CryptoCurrencyRepository repository) {
        this.repository = repository;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.coingecko.com/api/v3")
                .build();
    }

    // Runs 5 mins to fetch fresh data
    @Scheduled(fixedRate = 300000) // in milliseconds = 5mins
    public void fetchAndSaveCryptocurrencyData() {
        System.out.println("Fetching cryptocurrency data from CoinGecko...");

        try {
            // Call CoinGecko API
            String apiResponse = webClient
                    .get()
                    .uri("/simple/price?ids=bitcoin,ethereum,litecoin&vs_currencies=usd&include_24hr_change=true&include_24hr_vol=true")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("API Response: " + apiResponse);

            // Parse and save data
            parseAndSaveData(apiResponse);

        } catch (Exception e) {
            System.err.println("Error fetching Crypto Data:" + e.getMessage());
        }
    }

    // Helper mothods Parse JSON
    private void parseAndSaveData(String jsonResponse) {
        System.out.println("Parsing and saving Crypto data...");

        try {
            // Manual JSON parsing
            if (jsonResponse.contains("bitcoin")) {
                saveBitcoinData(jsonResponse);
            }
            if (jsonResponse.contains("ethereum")) {
                saveEthereumData(jsonResponse);
            }
            if (jsonResponse.contains("litecoin")) {
                saveLitecoinData(jsonResponse);
            }

            System.out.println("Successfully saved cryptocurrency data!");
        } catch (Exception e) {
            System.err.println("Error parsing cryptocurrency data: " + e.getMessage());
        }
    }

    private void saveBitcoinData(String jsonResponse) {
        // Extract Bitcoin data from JSON
        BigDecimal price = extractPrice(jsonResponse, "bitcoin", "usd");
        BigDecimal change = extractChange(jsonResponse, "bitcoin", "usd_24h_change");
        BigDecimal volume = extractVolume(jsonResponse, "bitcoin", "usd_24h_vol");

        // create or update Bitcoin record
        CryptoCurrency bitcoin = repository.findByCoinId("bitcoin")
                .orElse(new CryptoCurrency("bitcoin", "Bitcoin", price, change, volume));

        bitcoin.setPriceUsd(price);
        bitcoin.setChangePercent24h(change);
        bitcoin.setVolume24h(volume);

        repository.save(bitcoin);
        System.out.println("Bitcoin saved: $" + price);
    }

    private void saveEthereumData(String jsonResponse) {
        BigDecimal price = extractPrice(jsonResponse, "ethereum", "usd");
        BigDecimal change = extractChange(jsonResponse, "ethereum", "usd_24h_change");
        BigDecimal volume = extractVolume(jsonResponse, "ethereum", "usd_24h_vol");

        CryptoCurrency ethereum = repository.findByCoinId("ethereum")
                .orElse(new CryptoCurrency("ethereum", "Ethereum", price, change, volume));

        ethereum.setPriceUsd(price);
        ethereum.setChangePercent24h(change);
        ethereum.setVolume24h(volume);

        repository.save(ethereum);
        System.out.println("Ethereum saved: $" + price);
    }

    private void saveLitecoinData(String jsonResponse) {
        BigDecimal price = extractPrice(jsonResponse, "litecoin", "usd");
        BigDecimal change = extractChange(jsonResponse, "litecoin", "usd_24h_change");
        BigDecimal volume = extractVolume(jsonResponse, "litecoin", "usd_24h_vol");

        CryptoCurrency litecoin = repository.findByCoinId("litecoin")
                .orElse(new CryptoCurrency("litecoin", "Litecoin", price, change, volume));

        litecoin.setPriceUsd(price);
        litecoin.setChangePercent24h(change);
        litecoin.setVolume24h(volume);

        repository.save(litecoin);
        System.out.println("Litecoin saved: $" + price);
    }

    private BigDecimal extractPrice(String json, String coin, String field) {
        String pattern = "\"" + coin + "\":\\{[^}]*\"" + field + "\":(\\d+\\.?\\d*)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return new BigDecimal(m.group(1));
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal extractChange(String json, String coin, String field) {
        String pattern = "\"" + coin + "\":\\{[^}]*\"" + field + "\":(-?\\d+\\.?\\d*)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return new BigDecimal(m.group(1));
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal extractVolume(String json, String coin, String field) {
        String pattern = "\"" + coin + "\":\\{[^}]*\"" + field + "\":(\\d+\\.?\\d*)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return new BigDecimal(m.group(1));
        }
        return BigDecimal.ZERO;
    }

    public List<CryptoCurrency> getAllCryptoCurrencies() {
        return repository.findAll();
    }

    public Optional<CryptoCurrency> getCryptocurrencyByCoinId(String coinId) {
        return repository.findByCoinId(coinId);
    }

    public List<CryptoCurrency> getTopGainers() {
        return repository.findGainersOrderedByChange();
    }

    public List<CryptoCurrency> getTopLosers() {
        return repository.findLosersOrderedByChange();
    }

    public List<CryptoCurrency> getCryptoCurrenciesByPriceDesc() {
        return repository.findAllByOrderByPriceUsdDesc();
    }

}
