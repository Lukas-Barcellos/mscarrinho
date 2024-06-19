package br.com.fiap.mscarrinho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CarrinhoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarrinhoApplication.class, args);
	}

}
