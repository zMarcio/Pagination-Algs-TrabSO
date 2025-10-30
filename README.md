# 📊 Simulador de Algoritmos de Substituição de Páginas

## 📖 Introdução

Este projeto implementa e compara **quatro algoritmos clássicos de substituição de páginas** utilizados em sistemas operacionais para gerenciamento de memória virtual. O simulador permite visualizar o comportamento de cada algoritmo passo a passo e comparar o número de faltas de página geradas.

### O que são Algoritmos de Substituição de Páginas?

Em sistemas operacionais com memória virtual, quando a memória física (RAM) está cheia e uma nova página precisa ser carregada, o sistema deve escolher qual página existente será removida (substituída). Os algoritmos de substituição determinam **qual página será escolhida como vítima** para ser removida da memória.

---

## 🔍 Algoritmos Implementados

### 1. **FIFO (First In, First Out)**

**Descrição:**  
O algoritmo FIFO remove a página que está há **mais tempo na memória**, independentemente de seu uso recente. É o algoritmo mais simples de implementar.

**Como funciona:**
- Mantém uma fila de páginas na ordem de chegada
- Quando ocorre uma falta de página e a memória está cheia, remove a página no início da fila (mais antiga)
- Adiciona a nova página no final da fila

**Vantagens:**
- Simples de implementar
- Baixo overhead computacional

**Desvantagens:**
- Pode remover páginas frequentemente usadas
- Sofre da "Anomalia de Belady" (mais frames podem gerar mais faltas)

---

### 2. **LRU (Least Recently Used)**

**Descrição:**  
O algoritmo LRU remove a página que **não foi utilizada há mais tempo**. Baseia-se no princípio de localidade temporal: páginas usadas recentemente tendem a ser usadas novamente em breve.

**Como funciona:**
- Mantém registro do tempo de último acesso de cada página
- Quando ocorre uma falta de página, remove a página com o acesso mais antigo
- Atualiza o registro sempre que uma página é acessada

**Vantagens:**
- Melhor desempenho que FIFO na maioria dos casos
- Considera o padrão de uso das páginas

**Desvantagens:**
- Maior overhead para manter o histórico de acessos
- Implementação mais complexa em hardware real

---

### 3. **Relógio (Clock)**

**Descrição:**  
O algoritmo Relógio (ou Clock) é uma aproximação eficiente do LRU. Utiliza um **bit de referência** para cada página e um ponteiro que circula pelas páginas como um relógio.

**Como funciona:**
- Cada página possui um bit de referência (0 ou 1)
- Um ponteiro circular percorre as páginas
- Quando ocorre acesso, o bit é definido como 1
- Para substituir: o ponteiro avança, se encontrar bit=0, substitui; se bit=1, muda para 0 e continua

**Vantagens:**
- Aproximação eficiente do LRU
- Baixo overhead computacional
- Amplamente usado em sistemas reais

**Desvantagens:**
- Não é tão preciso quanto o LRU verdadeiro
- Desempenho depende da velocidade de varredura

---

### 4. **Ótimo (Optimal)**

**Descrição:**  
O algoritmo Ótimo (ou OPT) remove a página que **não será usada por mais tempo no futuro**. É o algoritmo teoricamente perfeito, mas **impraticável na realidade** pois requer conhecimento futuro.

**Como funciona:**
- Analisa toda a sequência futura de referências
- Quando precisa substituir, escolhe a página que será referenciada mais tarde
- Se alguma página nunca mais for usada, ela é escolhida primeiro

**Vantagens:**
- Gera o **menor número possível de faltas de página**
- Usado como referência para comparar outros algoritmos

**Desvantagens:**
- **Impossível de implementar em sistemas reais** (requer conhecimento do futuro)
- Usado apenas para fins acadêmicos e comparação

---

## 🖥️ Interface Gráfica

O projeto inclui uma **interface gráfica interativa** (`PageReplacementSimulatorGUI.java`) que permite:

- ✅ Inserir sequência de páginas personalizada
- ✅ Definir número de frames (molduras de memória)
- ✅ Escolher algoritmo individual ou comparar todos simultaneamente
- ✅ **Navegação passo a passo** pelos acessos à memória
- ✅ Visualização em tabela com indicação de faltas
- ✅ Gráfico comparativo automático ao finalizar
- ✅ Tutorial integrado explicando o uso da interface

---

## ⚙️ Pré-requisitos

Para executar o simulador, você precisa de:

### 1. **Java Development Kit (JDK)**
- **Versão mínima:** JDK 11 ou superior
- **Versão recomendada:** JDK 17+ ou JDK 21+

#### Verificar se o Java está instalado:
```bash
java -version
javac -version
```

#### Download do JDK:
- Oracle JDK: https://www.oracle.com/java/technologies/downloads/
- OpenJDK: https://adoptium.net/

### 2. **Sistema Operacional**
- Windows, Linux ou macOS
- Interface gráfica suportada (para GUI Swing)

### 3. **Terminal/Prompt de Comando**
- Windows: PowerShell, CMD ou Windows Terminal
- Linux/macOS: Terminal bash/zsh

---

## 🚀 Como Executar

### **Opção 1: Interface Gráfica (Recomendado)**

#### 1. Compilar os arquivos:
```bash
javac PageReplacementSimulator.java PageReplacementSimulatorGUI.java
```

#### 2. Executar a interface gráfica:
```bash
java PageReplacementSimulatorGUI
```

#### 3. Utilizar a interface:
1. **Tutorial inicial:** Ao abrir, um tutorial explica todos os recursos
2. **Inserir sequência:** Digite a sequência de páginas (ex: `7,0,1,2,0,3,0,4,2,3,0,3,2`)
3. **Definir frames:** Escolha o número de molduras (ex: `3`)
4. **Escolher algoritmo:** Selecione um algoritmo específico ou "Todos"
5. **Executar:** Clique no botão "▶ Executar"
6. **Navegar:** Use os botões "◀ Anterior" e "Próximo ▶" para ver cada passo
7. **Gráfico:** O gráfico aparece automaticamente ao chegar no último passo

---

### **Opção 2: Linha de Comando (Console)**

#### 1. Compilar:
```bash
javac PageReplacementSimulator.java
```

#### 2. Executar com argumentos:
```bash
java PageReplacementSimulator "7,0,1,2,0,3,0,4,2,3,0,3,2" 3 --verbose --gui
```

**Parâmetros:**
- `"7,0,1,2,0,3,0,4,2,3,0,3,2"` → Sequência de páginas (entre aspas)
- `3` → Número de frames
- `--verbose` → Mostra tabela passo a passo no console
- `--gui` → Abre janela com gráfico de barras

#### 3. Executar no modo interativo:
```bash
java PageReplacementSimulator
```

O programa perguntará:
1. Sequência de páginas
2. Número de molduras
3. Se deseja visualização passo a passo
4. Se deseja abrir o gráfico

---

## 📊 Exemplo de Execução

### **Entrada Padrão:**
- **Sequência:** `7,0,1,2,0,3,0,4,2,3,0,3,2`
- **Frames:** `3`
- **Algoritmos:** Todos (FIFO, LRU, Relógio, Ótimo)

### **Resultados Obtidos:**

#### **Resumo (Faltas de Página):**

| Algoritmo | Número de Faltas |
|-----------|------------------|
| **FIFO**  | 9 faltas         |
| **LRU**   | 10 faltas        |
| **Relógio** | 9 faltas       |
| **Ótimo** | 7 faltas         |

#### **Análise dos Resultados:**

1. **Ótimo** teve o melhor desempenho com apenas **7 faltas**, confirmando ser o algoritmo teoricamente perfeito
2. **FIFO** e **Relógio** empataram com **9 faltas**, mostrando boa eficiência para esta sequência
3. **LRU** teve o pior desempenho com **10 faltas**, o que pode ocorrer dependendo do padrão de acesso

**Observação:** Os resultados variam conforme a sequência de páginas. O algoritmo Ótimo sempre terá o menor número de faltas.

---

### **Visualização Passo a Passo (Exemplo FIFO):**

```
== FIFO ==
Ref | F0 F1 F2 | Falta
  7 | 7  -  -  | *
  0 | 7  0  -  | *
  1 | 7  0  1  | *
  2 | 2  0  1  | *
  0 | 2  0  1  |  
  3 | 2  3  1  | *
  0 | 0  3  1  | *
  4 | 0  3  4  | *
  2 | 2  3  4  | *
  3 | 2  3  4  |  
  0 | 2  0  4  | *
  3 | 2  0  3  | *
  2 | 2  0  3  |  
Total de faltas: 9
```

**Legenda:**
- `F0, F1, F2` → Frames (molduras de memória)
- `*` → Indica que ocorreu uma falta de página
- `-` → Frame vazio

---

## 📁 Estrutura do Projeto

```
trabSO/
│
├── PageReplacementSimulator.java      # Classe principal com algoritmos
├── PageReplacementSimulatorGUI.java   # Interface gráfica Swing
├── README.md                           # Este arquivo
├── trabSO.iml                          # Arquivo de configuração IntelliJ
└── (arquivos .class gerados após compilação)
```

---

## 🎯 Funcionalidades

### **Simulador Console:**
- ✅ Implementação dos 4 algoritmos clássicos
- ✅ Modo interativo ou via argumentos
- ✅ Visualização passo a passo em tabela ASCII
- ✅ Gráfico de barras comparativo (Swing)
- ✅ Exportação do gráfico para PNG

### **Interface Gráfica (GUI):**
- ✅ Tutorial modal ao iniciar
- ✅ Design profissional com cores customizadas
- ✅ Navegação passo a passo sincronizada
- ✅ Modo de comparação simultânea ("Todos")
- ✅ Tabelas separadas por algoritmo
- ✅ Gráfico automático ao finalizar navegação
- ✅ Validação de entrada de dados

---

## 🔧 Personalização

### **Modificar a Sequência Padrão:**
Edite o método `main()` em `PageReplacementSimulator.java` ou use a interface gráfica.

### **Ajustar Número de Frames:**
Modifique o segundo parâmetro na linha de comando ou use o spinner na GUI.

### **Alterar Cores da Interface:**
Em `PageReplacementSimulatorGUI.java`, modifique as constantes de cor:
```java
private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
// ... outras cores
```

---

## 📚 Conceitos de Sistemas Operacionais

### **Falta de Página (Page Fault):**
Ocorre quando um programa tenta acessar uma página que não está na memória física (RAM). O sistema deve:
1. Pausar o processo
2. Carregar a página do disco
3. Substituir uma página existente (se memória cheia)
4. Retomar a execução

### **Frame (Moldura):**
Unidade de memória física onde uma página é armazenada. O número de frames determina quantas páginas podem estar na memória simultaneamente.

### **Página:**
Unidade de memória virtual. Um processo divide sua memória em páginas de tamanho fixo.

### **Princípio de Localidade:**
- **Temporal:** Páginas recentemente acessadas tendem a ser acessadas novamente
- **Espacial:** Páginas próximas a uma página acessada tendem a ser acessadas em seguida

---

## 🎓 Uso Acadêmico

Este simulador é ideal para:
- 📖 Disciplinas de **Sistemas Operacionais**
- 🧪 Laboratórios de **Gerenciamento de Memória**
- 📊 Trabalhos de **Análise de Desempenho**
- 🎯 Comparação prática entre algoritmos teóricos

---

## 👨‍💻 Desenvolvimento

### **Tecnologias Utilizadas:**
- **Linguagem:** Java 11+
- **Interface Gráfica:** Swing (javax.swing)
- **Estruturas de Dados:** Queue, LinkedHashMap, ArrayList, Set
- **Design Pattern:** MVC (Model-View-Controller)

### **Arquitetura:**
- **Model:** Classes `Result` e `Step` para armazenar estados
- **View:** `PageReplacementSimulatorGUI` (interface visual)
- **Controller:** Métodos de simulação em `PageReplacementSimulator`

---

## 📄 Licença

Este projeto foi desenvolvido para fins educacionais.

---

## 📧 Suporte

Para dúvidas ou sugestões sobre o simulador:
- Consulte a documentação inline no código
- Verifique os comentários nos métodos de simulação
- Use o tutorial integrado na interface gráfica

---

## 🚀 Próximos Passos

Sugestões de melhorias futuras:
- [ ] Implementar algoritmo LFU (Least Frequently Used)
- [ ] Adicionar algoritmo Second Chance
- [ ] Exportar resultados para CSV
- [ ] Modo de comparação com múltiplas sequências
- [ ] Gráfico de linha mostrando faltas acumuladas

---

## 🎯 Conclusão

A execução do simulador permitiu comparar o comportamento prático dos principais algoritmos de substituição de páginas. Com a sequência de teste utilizada, observou-se que o algoritmo **Ótimo** apresentou o melhor desempenho, com apenas **7 faltas de página**, confirmando seu caráter teórico ideal.

Os algoritmos **LRU** e **Relógio** obtiveram resultados semelhantes, ambos com **9 faltas de página**, demonstrando que o Relógio é uma boa aproximação do LRU, porém com menor custo de implementação. Já o **FIFO**, embora simples, foi o menos eficiente, registrando **10 faltas de página** e evidenciando a possibilidade de **anomalia de Belady**, em que o aumento do número de molduras não necessariamente reduz as faltas.

O simulador desenvolvido mostrou-se uma ferramenta eficaz para visualizar e compreender o impacto das diferentes políticas de substituição de páginas no desempenho de um sistema de memória virtual.

---

**Desenvolvido como material educacional para Sistemas Operacionais** 🎓
