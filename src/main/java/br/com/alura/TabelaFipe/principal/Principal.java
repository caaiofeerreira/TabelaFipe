package br.com.alura.TabelaFipe.principal;

import br.com.alura.TabelaFipe.model.DadosVeiculos;
import br.com.alura.TabelaFipe.model.Modelos;
import br.com.alura.TabelaFipe.model.Veiculo;
import br.com.alura.TabelaFipe.service.ConsumoAPI;
import br.com.alura.TabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Principal {

    private Scanner scanner = new Scanner(System.in);
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    public void exibeMenu() {

        var menu = """
                ***** TABELA FIPE *****
                
                1 - CARRO
                2 - MOTO
                3 - CAMINHÃO
                
                ***********************
                """;

        System.out.println(menu);
        var opcao = scanner.nextInt();
        scanner.nextLine();

        String endereco = "";

        if (opcao == 1) {
            endereco = URL_BASE + "carros/marcas";
        } else if (opcao == 2) {
            endereco = URL_BASE + "motos/marcas";
        } else if (opcao == 3) {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumoAPI.obterDados(endereco);
        // System.out.println(json);

        var marcas = conversor.obterLista(json, DadosVeiculos.class);
        marcas.stream().sorted(Comparator.comparing(DadosVeiculos::codigo))
                .forEach(System.out::println);

        System.out.println("\n-------------------------------------------");
        System.out.println("Informe o código da marca para consulta:");

        var codigoMarca = scanner.nextLine();

        endereco += "/" + codigoMarca + "/modelos";
        json = consumoAPI.obterDados(endereco);
        var modelosLista = conversor.obterDados(json, Modelos.class);

       if (modelosLista != null && modelosLista.modelos() != null) {
            System.out.println("\nModelos da marca: ");
           modelosLista.modelos().stream()
                    .sorted(Comparator.comparing(DadosVeiculos::codigo))
                    .forEach(System.out::println);
       } else {
            System.out.println("Nenhum modelo encontrado.");
       }

       System.out.println("\n-------------------------------------------");
       System.out.println("Digite o nome do veiculo: ");

       var modelosNome = scanner.nextLine();
       List<DadosVeiculos> modelosFintrados = modelosLista.modelos().stream()
               .filter(m -> m.nome().toLowerCase().contains(modelosNome.toLowerCase())).toList();

       System.out.println("\nVeiculos encontrados:");
       modelosFintrados.forEach(System.out::println);

        System.out.println("\nDigite o código do veiculo: ");
        var codigoModelos = scanner.nextLine();

        endereco += "/" + codigoModelos + "/anos";
        json = consumoAPI.obterDados(endereco);
        List<DadosVeiculos> anos = conversor.obterLista(json, DadosVeiculos.class);

        List<Veiculo> veiculos = new ArrayList<>();
        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumoAPI.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\n-------------------------------------------");
        System.out.println("Veículos filtrados por ano: ");
        System.out.printf("%-15s | %-27s | %-10s | %-15s | %-15s%n", "Marca", "Modelo", "Ano", "Combustível", "Valor");
        System.out.println("----------------------------------------------------------------------------------------");
        veiculos.forEach(veiculo -> System.out.printf("%-15s | %-15s | %-10d | %-15s | %-15s%n",
                veiculo.marca(), veiculo.modelo(), veiculo.ano(), veiculo.combustivel(), veiculo.valor()));
    }
}
