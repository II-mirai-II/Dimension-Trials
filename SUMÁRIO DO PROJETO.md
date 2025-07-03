# SUMÁRIO DO PROJETO

Este documento mapeia e explica a estrutura do mod Dimension Trials, servindo como referência rápida para compreender a organização e funcionalidades do projeto.

## Índice
- [Visão Geral](#visão-geral)
- [Estrutura de Pacotes](#estrutura-de-pacotes)
- [Classe Principal](#classe-principal)
- [Sistemas Principais](#sistema-de-progressão)
  - [Sistema de Progressão](#sistema-de-progressão)
  - [Sistema de Parties](#sistema-de-parties)
  - [Sistema de Networking](#sistema-de-networking)
  - [Sistema de Configuração](#sistema-de-configuração)
  - [Sistema de Requisitos Personalizados](#sistema-de-requisitos-personalizados)
- [Client](#client)
  - [Dados de Progressão](#dados-de-progressão-client)
  - [GUI](#gui)
    - [Screens](#screens)
    - [Sections](#sections)
    - [Implementações de Seções Específicas](#implementações-de-seções-específicas)
- [Data](#data)
  - [Sistema de Parties](#sistema-de-parties-data)
  - [Coordenação de Progressão](#coordenação-de-progressão)
- [Utilidades e Constantes](#utilidades-e-constantes)
- [Integração com Mods Externos](#integração-com-mods-externos)
- [Comandos](#comandos)
- [Conclusão](#conclusão)
- [Utilidades e Constantes](#utilidades-e-constantes)
- [Integração com Mods Externos](#integração-com-mods-externos)
- [Comandos](#comandos)
- [Conclusão](#conclusão)

## Visão Geral

Dimension Trials é um mod para Minecraft que implementa um sistema de progressão em fases e desafios para jogadores. O mod usa o NeoForge como framework base e implementa vários sistemas interconectados:

1. **Sistema de Progressão por Fases**: Jogadores avançam através de diferentes fases completando objetivos específicos
2. **Sistema de Parties Colaborativas**: Permite que jogadores formem grupos para progredir coletivamente
3. **Interface de HUD Interativa**: Exibe o progresso do jogador de forma intuitiva e organizada
4. **Sistema de Configuração Abrangente**: Permite personalizar todos os aspectos do mod
5. **Integração com Outros Mods**: Suporte para extensão e integração com outros mods

## Estrutura de Pacotes

O projeto é organizado principalmente no pacote base `net.mirai.dimtr`, com as seguintes subpastas principais:

- `client`: Componentes exclusivos do lado do cliente
- `command`: Comandos administrativos e de party
- `config`: Sistema de configuração e requisitos personalizados
- `data`: Sistema de gerenciamento de dados (progressão, parties)
- `integration`: Integração com mods externos
- `network`: Sistema de comunicação cliente-servidor
- `util`: Classes utilitárias e constantes

## Client

### Dados de Progressão Client

#### `ClientProgressionData`

Classe singleton que armazena e gerencia os dados de progressão do jogador no cliente.

**Localização**: `net.mirai.dimtr.client.ClientProgressionData`

**Responsabilidades**:
- Armazenar o estado atual da progressão do jogador
- Receber e processar atualizações de dados do servidor
- Fornecer métodos para verificar a conclusão de fases e objetivos

**Campos Principais**:
- **Objetivos Principais**:
  - `elderGuardianKilled`: Indica se o Elder Guardian foi derrotado
  - `raidWon`: Indica se uma raid foi concluída com sucesso
  - `trialVaultAdvancementEarned`: Conquista relacionada ao Trial Vault
  - `voluntaireExileAdvancementEarned`: Conquista Voluntary Exile
  - `phase1Completed`: Estado de conclusão da Fase 1
  - `witherKilled`: Indica se o Wither foi derrotado
  - `wardenKilled`: Indica se o Warden foi derrotado
  - `phase2Completed`: Estado de conclusão da Fase 2

- **Contadores de Mobs (Fase 1)**:
  - Rastreamento de mortes de mobs do Overworld (ex: zombie, skeleton, creeper, etc.)
  - Valores atuais e requisitos configuráveis

- **Contadores de Mobs (Fase 2)**:
  - Rastreamento de mortes de mobs do Nether (ex: blaze, wither skeleton, etc.)
  - Valores atuais e requisitos configuráveis

- **Fases Personalizadas**:
  - `customPhaseCompletion`: Mapeia fases personalizadas e seu estado de conclusão
  - `customMobKills`: Rastreia mortes de mobs para fases personalizadas
  - `customObjectiveCompletion`: Rastreia conclusão de objetivos personalizados

**Métodos Importantes**:
- `updateData(UpdateProgressionToClientPayload)`: Atualiza os dados de progressão com informações do servidor
- `isPhase1EffectivelyComplete()`: Verifica se a Fase 1 está efetivamente completa
- Vários getters para acessar campos privados

### GUI

#### Screens

##### `ProgressionHUDScreen`
**Arquivo:** `net.mirai.dimtr.client.gui.screens.ProgressionHUDScreen`

Interface gráfica principal para visualização do progresso do jogador, implementada com design modular e sistema de scroll vertical.

**Funcionalidades principais:**
- Sistema de navegação entre uma visão geral (sumário) e seções específicas de conteúdo
- Layout responsivo que se adapta às dimensões da tela do jogador
- Sistema de scroll vertical para navegação em listas extensas de seções
- Paginação para visualização de conteúdo extenso dentro de seções específicas
- Integração com o sistema de Parties
- Feedback visual e sonoro para interações do usuário

**Estados de navegação:**
- `SUMMARY`: Exibe visão geral com todas as seções disponíveis
- `SECTION`: Exibe conteúdo detalhado de uma seção específica

**Sistema de renderização:**
- Renderização dinâmica baseada em dimensões calculadas proporcionalmente à tela
- Sistema de clipping para controle de visibilidade durante o scroll
- Renderização de scrollbar interativa com feedback visual
- Separação de conteúdo em colunas para melhor legibilidade

**Interatividade:**
- Controles de teclado para navegação (setas, ESC, teclas Q/E para paginação)
- Controles de mouse para seleção de seções e scroll
- Feedback sonoro para todas as interações

**Integração:**
- Utiliza o `SectionManager` para gerenciar módulos de conteúdo
- Consome dados do `ClientProgressionData` para exibir progresso atual
- Implementa interface modular via `HUDSection` para expansibilidade

**Evolução:**
- Sistema inicialmente baseado em tabs evoluiu para um sistema de scroll vertical
- Suporte a múltiplas fases de progressão incluindo fases customizadas
- Adaptação para diferentes resoluções de tela

**Detalhes técnicos:**
- Estende a classe `Screen` do Minecraft
- Implementa cálculos dinâmicos para posicionamento de elementos
- Utiliza sistema de scissor/clipping para visualização parcial durante scroll

#### Sections

##### `HUDSection`
**Arquivo:** `net.mirai.dimtr.client.gui.sections.HUDSection`

Interface base que define o contrato para todas as seções exibidas no HUD de progressão, garantindo modularidade e extensibilidade.

**Funcionalidades principais:**
- Define métodos para obtenção de identificação, título e conteúdo da seção
- Estabelece o padrão para verificação de acessibilidade baseada no progresso do jogador
- Permite geração dinâmica de conteúdo contextual baseado no progresso atual

**Métodos principais:**
- `getType()`: Retorna o tipo único da seção via enum `SectionType`
- `getTitle()`: Retorna o componente de texto para o título da seção
- `getDescription()`: Retorna a descrição que será exibida no sumário
- `getIcon()`: Retorna o ícone (emoji) que representa visualmente a seção
- `isAccessible()`: Determina se a seção está disponível com base no progresso
- `generateContent()`: Gera dinamicamente o conteúdo textual da seção

**Enum SectionType:**
- Define tipos de seções suportadas com suas chaves de tradução e ícones
- Inclui seções para fases principais (1, 2, 3), objetivos específicos, grupos e fases customizadas
- Cada tipo armazena sua chave de tradução e ícone para consistência visual

**Integração:**
- Utilizada pelo `SectionManager` para registrar e gerenciar seções disponíveis
- Implementada por todas as seções específicas do HUD
- Consumida pelo `ProgressionHUDScreen` para renderização dinâmica

##### `Phase2GoalsSection`

**Arquivo:** `net.mirai.dimtr.client.gui.sections.Phase2GoalsSection`

Implementação da interface `HUDSection` focada nos objetivos de eliminação de mobs da Fase 2 (Nether).

**Funcionalidades principais:**
- Exibe contadores detalhados de mobs eliminados na Fase 2
- Organiza os mobs em três categorias: Nether, Overworld repetidos e mobs-objetivo
- Mostra progresso atual versus requisitos aumentados para esta fase
- Fornece resumo estatístico separado para progresso no Nether e no Overworld

**Categorias de mobs:**
- **Mobs do Nether**: Blaze, Wither Skeleton, Piglin Brute, Hoglin, Zoglin, Ghast, Piglin
- **Mobs do Overworld (requisitos aumentados)**: Zombie, Skeleton, Creeper, Spider, Enderman, Witch, Pillager
- **Mobs-Objetivo resetados**: Ravager, Evoker (novos requisitos para a Fase 2)

**Estatísticas e resumo:**
- Progresso do Nether: contagem de tipos de mobs do Nether completos
- Progresso do Overworld: contagem de tipos de mobs do Overworld completos
- Formatação visual diferenciada para os dois tipos de progresso

**Comportamento contextual:**
- Verifica se a Fase 1 está efetivamente completa para permitir acesso
- Exibe mensagem informativa quando o acesso está bloqueado
- Verifica se o sistema de eliminação de mobs da Fase 2 está habilitado
- Exibe mensagens apropriadas quando sistemas estão desabilitados

**Tratamento de dados:**
- Acessa dados de eliminações via mapa centralizado de contadores
- Verifica requisitos específicos para a Fase 2 (geralmente mais elevados)
- Aplica lógica de comparação para determinar status de conclusão
- Organiza os contadores de forma clara e com categorização lógica

###### `Phase2MainSection`

Implementação da `HUDSection` dedicada à exibição da visão geral e objetivos principais da Fase 2 (Nether).

**Localização**: `net.mirai.dimtr.client.gui.sections.Phase2MainSection`

**Análise Linha a Linha**:

```java
package net.mirai.dimtr.client.gui.sections;
```
- Define o pacote onde a classe está localizada, agrupada com outras implementações de seções do HUD.

```java
import net.mirai.dimtr.client.ClientProgressionData;
import net.mirai.dimtr.util.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
```
- Importações revelam dependências essenciais:
  - `ClientProgressionData`: Dados de progressão do jogador
  - `Constants`: Chaves de tradução e constantes
  - `ChatFormatting`: Formatação visual (cores) do texto
  - `Component`: Sistema de texto localizado do Minecraft
  - `ArrayList`/`List`: Estruturas para construir o conteúdo da seção

```java
/**
 * Seção principal da Fase 2
 */
public class Phase2MainSection implements HUDSection {
```
- Comentário descreve o propósito da seção: apresentar a visão principal da Fase 2
- Implementa a interface `HUDSection` para integração no sistema modular de seções

```java
    @Override
    public SectionType getType() {
        return SectionType.PHASE2_MAIN;
    }
```
- Implementação do método `getType()` da interface
- Retorna o tipo enum específico para esta seção: `PHASE2_MAIN`
- Permite que o `SectionManager` identifique e registre esta seção corretamente

```java
    @Override
    public Component getTitle() {
        return Component.literal(getIcon() + " ")
                .append(Component.translatable(getType().getTitleKey()));
    }
```
- Cria um título composto por:
  - Um componente literal com o ícone da seção (emoji 🌌)
  - Um espaço
  - Um componente traduzível usando a chave do tipo de seção
- Permite localização do título em diferentes idiomas

```java
    @Override
    public Component getDescription() {
        return Component.translatable("gui.dimtr.summary.phase2_main.desc");
    }
```
- Retorna a descrição localizada da seção
- Usa diretamente a chave de tradução ao invés de uma constante

```java
    @Override
    public String getIcon() {
        return getType().getIcon();
    }
```
- Implementação simples que delega para o ícone definido no enum `SectionType`
- Mantém consistência visual com a definição central de ícones

```java
    @Override
    public boolean isAccessible(ClientProgressionData progress) {
        return progress.isServerEnablePhase2() && progress.isPhase1EffectivelyComplete();
    }
```
- Define a lógica de acessibilidade para esta seção com duas condições:
  - Verifica se a Fase 2 está habilitada na configuração do servidor
  - Verifica se a Fase 1 foi efetivamente concluída pelo jogador
- Não verifica rastreamento de mobs (diferente de `Phase2GoalsSection`)
- Mais acessível que a seção de objetivos, priorizando visibilidade da visão geral

```java
    @Override
    public List<Component> generateContent(ClientProgressionData progress) {
        // 🎯 NOVO: Ensure client-side external mod integration is initialized
        net.mirai.dimtr.integration.ExternalModIntegration.initializeClientSide();
```
- Início do método principal que gera todo o conteúdo da seção
- Comentário indica adição recente: inicialização da integração com mods externos
- Chamada para garantir que a integração com mods externos esteja inicializada
- Consistente com `Phase1MainSection`, demonstrando padrão uniforme

```java
        List<Component> content = new ArrayList<>();

        if (!progress.isPhase1EffectivelyComplete()) {
            content.add(Component.translatable("gui.dimtr.complete.phase1.first")
                    .withStyle(ChatFormatting.RED));
            content.add(Component.empty());
            content.add(Component.translatable("gui.dimtr.phase2.locked.line1")
                    .withStyle(ChatFormatting.GRAY));
            content.add(Component.translatable("gui.dimtr.phase2.locked.line2")
                    .withStyle(ChatFormatting.GRAY));
            content.add(Component.translatable("gui.dimtr.phase2.locked.line3")
                    .withStyle(ChatFormatting.GRAY));
            return content;
        }
```
- Cria lista vazia para acumular componentes de texto
- Primeira verificação de segurança: fase anterior concluída
- Mensagem de erro em vermelho se a Fase 1 não estiver concluída
- Adiciona três linhas de explicação em cinza sobre o porquê a Fase 2 está bloqueada
- Mais informativo que `Phase2GoalsSection`, fornecendo contexto adicional
- Padrão de "falha rápida" retornando imediatamente com as mensagens

```java
        if (!progress.isServerEnablePhase2()) {
            content.add(Component.translatable("gui.dimtr.phase2.disabled")
                    .withStyle(ChatFormatting.GRAY));
            return content;
        }
```
- Segunda verificação de segurança: Fase 2 habilitada no servidor
- Mensagem específica em cinza se a fase estiver desabilitada
- Continua o padrão de "falha rápida"

```java
        if (progress.isPhase2Completed()) {
            content.add(Component.translatable("gui.dimtr.phase.complete")
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            content.add(Component.empty());
        }
```
- Verificação se a Fase 2 já está concluída
- Em caso positivo, adiciona mensagem destacada em verde e negrito
- Adiciona linha vazia para espaçamento visual
- Não retorna imediatamente, continua mostrando detalhes mesmo com fase concluída
- Consistente com a abordagem de `Phase1MainSection`

```java
        // Objetivos especiais
        content.add(Component.translatable(Constants.HUD_SECTION_SPECIAL_OBJECTIVES)
                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
```
- Adiciona cabeçalho "Objetivos Especiais" em roxo claro e negrito
- Usa constante para a chave de tradução
- Diferente de `Phase1MainSection` que usa dourado, adaptado à temática do Nether

```java
        if (progress.isServerReqWither()) {
            content.add(createGoalLine(
                    Component.translatable(Constants.HUD_WITHER_KILLED),
                    progress.isWitherKilled()));
        }
```
- Verificação condicional para objetivo do Wither
- Verifica primeiro se o objetivo está habilitado no servidor via `isServerReqWither()`
- Só adiciona a linha se o objetivo estiver ativo na configuração
- Chamada método auxiliar `createGoalLine` que formata a linha com ícone de status
- Passa:
  - Componente traduzível para o texto do objetivo
  - Status de conclusão via `isWitherKilled()`
- Padrão consistente com `Phase1MainSection`

```java
        if (progress.isServerReqWarden()) {
            content.add(createGoalLine(
                    Component.translatable(Constants.HUD_WARDEN_KILLED),
                    progress.isWardenKilled()));
        }
```
- Verificação similar para o objetivo do Warden
- Segue o mesmo padrão do objetivo do Wither
- Demonstra consistência no tratamento dos objetivos principais

```java
        // 🎯 NOVO: Bosses de mods externos para Fase 2
        var externalBossesPhase2 = progress.getExternalBossesForPhase(2);
        if (!externalBossesPhase2.isEmpty()) {
            content.add(Component.empty());
            content.add(Component.translatable("gui.dimtr.external.bosses.phase2")
                    .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));

            for (var boss : externalBossesPhase2) {
                boolean killed = progress.isExternalBossKilled(boss.entityId);
                content.add(createGoalLine(
                        Component.literal(boss.displayName),
                        killed));
            }
        }
```
- Comentário indica funcionalidade recente: integração com bosses de mods externos
- Obtém lista de bosses externos definidos para a Fase 2
- Verifica se a lista não está vazia antes de adicionar seção
- Adiciona cabeçalho específico para bosses externos
- Itera sobre cada boss externo:
  - Verifica se o boss foi derrotado
  - Adiciona linha formatada usando o mesmo método auxiliar dos objetivos principais
  - Usa nome de exibição diretamente, sem tradução
- Segue o mesmo padrão de `Phase1MainSection`, demonstrando consistência arquitetural

```java
        content.add(Component.empty());

        // Status da fase
        if (progress.isPhase2Completed()) {
            content.add(Component.translatable("gui.dimtr.end.unlocked")
                    .withStyle(ChatFormatting.GREEN));
        } else {
            content.add(Component.translatable("gui.dimtr.complete.objectives")
                    .withStyle(ChatFormatting.YELLOW));
            content.add(Component.translatable("gui.dimtr.unlock.end")
                    .withStyle(ChatFormatting.YELLOW));
        }
```
- Linha vazia para separação visual
- Seção de status da fase que se adapta ao progresso atual:
  - Se fase completa: mensagem verde indicando que o End está desbloqueado
  - Se incompleta: duas mensagens amarelas orientando o jogador a completar objetivos para desbloquear o End
- Demonstra UI adaptativa com base no estado de progressão
- Padrão consistente com `Phase1MainSection`, apenas alterando a dimensão referenciada (End vs. Nether)

```java
        content.add(Component.empty());
        content.add(Component.translatable("gui.dimtr.unique.challenges")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        content.add(Component.translatable("gui.dimtr.challenge.wither")
                .withStyle(ChatFormatting.GRAY));
        content.add(Component.translatable("gui.dimtr.challenge.warden")
                .withStyle(ChatFormatting.GRAY));
        content.add(Component.translatable("gui.dimtr.challenge.nether")
                .withStyle(ChatFormatting.GRAY));
        content.add(Component.translatable("gui.dimtr.challenge.new.mobs")
                .withStyle(ChatFormatting.GRAY));
```
- Seção adicional não presente em `Phase1MainSection`
- Adiciona cabeçalho "Desafios Únicos" em dourado e negrito
- Lista quatro desafios específicos da Fase 2 em cinza:
  - Derrotar o Wither
  - Derrotar o Warden
  - Explorar o Nether
  - Enfrentar novos tipos de mobs
- Fornece contexto e orientação adicional sobre a temática da fase
- Demonstra personalização de conteúdo específico para cada fase

```java
        return content;
    }
```
- Retorna a lista completa de componentes gerados
- Todo o conteúdo é montado em memória e retornado para renderização

```java
    private Component createGoalLine(Component text, boolean completed) {
        ChatFormatting statusColor = completed ? ChatFormatting.DARK_GREEN : ChatFormatting.RED;
        String statusIcon = completed ? "✔" : "❌";

        return Component.literal(statusIcon + " ").withStyle(statusColor)
                .append(text.copy().withStyle(ChatFormatting.WHITE));
    }
```
- Método auxiliar que encapsula a formatação visual de uma linha de objetivo
- Parâmetros:
  - `text`: Componente com texto já traduzido para o objetivo
  - `completed`: Status de conclusão do objetivo
- Define cor e ícone baseados na conclusão:
  - Verde escuro e ✔ para concluído
  - Vermelho e ❌ para não concluído
- Constrói componente composto:
  - Ícone de status com cor apropriada
  - Texto do objetivo em branco (usando cópia do componente original)
- Idêntico ao método em `Phase1MainSection`, demonstrando consistência entre fases

**Responsabilidades Principais**:

1. **Apresentação de Objetivos Principais da Fase 2**
   - Exibe status dos objetivos especiais: Wither e Warden
   - Mostra apenas objetivos habilitados na configuração do servidor
   - Fornece feedback visual claro sobre conclusão (✔/❌)

2. **Integração com o End**
   - Mostra claramente se a fase está concluída
   - Informa sobre desbloqueio do End
   - Orienta o jogador sobre próximos passos

3. **Orientação Contextual**
   - Fornece mensagens detalhadas quando a fase está bloqueada
   - Apresenta lista de desafios únicos da Fase 2
   - Contextualiza a experiência do jogador no Nether

4. **Integração com Mods Externos**
   - Inicializa sistema de integração com mods externos
   - Exibe bosses de mods externos relevantes para a Fase 2
   - Mantém experiência consistente independente da origem do conteúdo

**Técnicas de Design Implementadas**:

1. **Conteúdo Adaptativo**
   - Exibe diferentes mensagens baseadas no progresso atual
   - Fornece explicações detalhadas quando a fase está bloqueada
   - Mostra desafios específicos relevantes para o contexto do Nether

2. **Consistência Temática**
   - Uso de roxo claro para cabeçalhos (tema do Nether)
   - Referências específicas a desafios do Nether
   - Mensagens contextualizadas para a segunda fase da progressão

3. **Verificação de Pré-requisitos**
   - Verificação clara da conclusão da Fase 1
   - Mensagens informativas sobre o motivo do bloqueio
   - Redirecionamento apropriado para completar a fase anterior

4. **Reutilização de Padrões**
   - Mantém estrutura similar à `Phase1MainSection` para experiência consistente
   - Usa mesmo método auxiliar para formatação de objetivos
   - Aplica mesmos padrões de verificação e feedback visual

**Diferenças com `Phase1MainSection`**:

1. **Mensagens de Bloqueio Expandidas**
   - `Phase2MainSection`: Fornece três linhas de explicação quando bloqueada
   - `Phase1MainSection`: Mensagem simples de fase desabilitada

2. **Seção de Desafios Únicos**
   - `Phase2MainSection`: Inclui seção adicional de desafios específicos
   - `Phase1MainSection`: Não possui esta seção contextual

3. **Objetivos Adaptados**
   - `Phase2MainSection`: Foca em Wither e Warden
   - `Phase1MainSection`: Foca em Elder Guardian, Raid e conquistas específicas

4. **Cores Temáticas**
   - `Phase2MainSection`: Usa roxo claro para cabeçalhos de objetivos (temática do Nether)
   - `Phase1MainSection`: Usa dourado para cabeçalhos de objetivos (temática do Overworld)

Estas diferenças demonstram como o sistema mantém consistência estrutural enquanto adapta o conteúdo e a apresentação visual para refletir a identidade única de cada fase da progressão.

##### `PartiesSection`
**Arquivo:** `net.mirai.dimtr.client.gui.sections.PartiesSection`

Implementação da interface `HUDSection` focada na exibição e gerenciamento de informações sobre grupos/parties de jogadores.

**Funcionalidades principais:**
- Exibe o status atual do jogador em relação a grupos (se está em um grupo ou não)
- Mostra detalhes do grupo atual (nome, tipo, membros, líder) quando aplicável
- Apresenta informações sobre progresso compartilhado entre membros do grupo
- Fornece instruções sobre comandos disponíveis para gerenciamento de grupos

**Comportamento contextual:**
- Quando em um grupo: exibe detalhes do grupo, lista de membros e progresso compartilhado
- Quando sem grupo: exibe comandos e instruções para criar ou entrar em grupos
- Indica o impacto do grupo no multiplicador de requisitos (aumento baseado no número de membros)
- Destaca o líder do grupo com um ícone de coroa (👑)

**Progresso compartilhado:**
- Exibe objetivos principais compartilhados (Elder Guardian, Raid, Wither, Warden)
- Mostra fases customizadas que foram compartilhadas entre os membros
- Utiliza formatação visual (cores e ícones) para indicar status de completude

**Benefícios de grupo:**
- Explica vantagens de jogar em grupo
- Detalha o funcionamento do compartilhamento de progresso
- Fornece informações sobre como o progresso é preservado ao sair de um grupo

**Integração:**
- Consome dados de `ClientPartyData` para obter informações atualizadas sobre grupos
- Utiliza constantes de `Constants` para exibição consistente de textos
- Registrada no `SectionManager` para exibição no HUD de progressão

##### `CustomPhasesSection`
**Arquivo:** `net.mirai.dimtr.client.gui.sections.CustomPhasesSection`

Implementação da interface `HUDSection` que gerencia a exibição de fases e objetivos customizados, incluindo integração com mods externos.

**Funcionalidades principais:**
- Exibe fases customizadas configuradas via sistema de `CustomRequirements`
- Integra-se com mods externos populares como Mowzie's Mobs e L_Ender's Cataclysm
- Apresenta status de conclusão para cada fase customizada
- Fornece informações sobre bosses de mods externos organizados por fase de progressão

**Detecção de mods:**
- Verifica dinamicamente a presença de mods compatíveis via FML ModList
- Adapta o conteúdo exibido com base nos mods instalados
- Suporta fallback para configurações customizadas quando mods não estão presentes

**Organização de conteúdo:**
- Agrupa bosses de Mowzie's Mobs na Fase 1 (Overworld)
- Distribui bosses de L_Ender's Cataclysm entre fases 1, 2 (Nether) e 3 (End)
- Exibe fases totalmente customizadas com nome, descrição e status de conclusão
- Utiliza formatação visual (cores e ícones) para indicar status e organização

**Acessibilidade:**
- A seção só é acessível quando mods compatíveis estão instalados ou fases customizadas estão configuradas
- Fornece mensagem explicativa quando não há fases customizadas disponíveis
- Inclui referência à configuração para usuários interessados em criar fases personalizadas

**Integração:**
- Consome dados de `ClientProgressionData` para verificar status de conclusão
- Utiliza `CustomRequirements` para obter definições de fases customizadas
- Registrada no `SectionManager` para exibição no HUD de progresso

##### SectionManager
**Arquivo:** `net.mirai.dimtr.client.gui.sections.SectionManager`

Gerenciador centralizado responsável por registrar, organizar e fornecer acesso a todas as seções do HUD de progressão.

**Funcionalidades principais:**
- Mantém um registro centralizado de todas as seções do HUD via mapa estático
- Inicializa todas as seções no bloco estático durante o carregamento da classe
- Fornece métodos para acessar seções específicas ou listar todas as seções disponíveis
- Filtra seções com base em seu estado de acessibilidade para um determinado progresso

**Métodos essenciais:**
- `register()`: Registra uma seção no gerenciador, associando-a ao seu tipo único
- `getSection()`: Recupera uma seção específica por seu tipo enumerado
- `getAllSections()`: Retorna uma lista com todas as seções registradas
- `getAccessibleSections()`: Filtra e retorna apenas as seções acessíveis com base no progresso atual

**Seções registradas:**
- `Phase1MainSection`: Visão geral da Fase 1 (Overworld)
- `Phase1GoalsSection`: Objetivos de eliminação de mobs da Fase 1
- `Phase2MainSection`: Visão geral da Fase 2 (Nether)
- `Phase2GoalsSection`: Objetivos de eliminação de mobs da Fase 2
- `Phase3MainSection`: Visão geral da Fase 3 (End)
- `PartiesSection`: Gerenciamento e informações de grupos de jogadores
- `CustomPhasesSection`: Fases e objetivos customizados, incluindo integração com mods externos

**Integração:**
- Consumido pelo `ProgressionHUDScreen` para obter as seções a serem renderizadas
- Centraliza a lógica de gerenciamento de seções, permitindo fácil expansão do sistema

##### `Phase1MainSection`
**Arquivo:** `net.mirai.dimtr.client.gui.sections.Phase1MainSection`

Implementação da interface `HUDSection` que exibe informações gerais e objetivos principais da Fase 1 (Overworld).

**Funcionalidades principais:**
- Exibe o status atual dos objetivos especiais da Fase 1
- Mostra indicadores visuais para objetivos completos e pendentes
- Apresenta resumo do progresso de eliminação de mobs quando este recurso está habilitado
- Integra-se com bosses de mods externos classificados para a Fase 1

**Objetivos monitorados:**
- Elder Guardian: Boss aquático que deve ser derrotado
- Raid: Evento que deve ser vencido em uma vila
- Trial Vault: Conquista relacionada aos baús de trial chambers
- Voluntaire Exile: Conquista relacionada a pillagers/saqueadores

**Comportamento contextual:**
- Exibe mensagem de conclusão quando a Fase 1 está completa
- Informa que o Nether está desbloqueado após a conclusão
- Mostra mensagens de instrução quando os objetivos ainda estão pendentes
- Respeita as configurações do servidor quanto a requisitos habilitados/desabilitados

**Integração com mods externos:**
- Inicializa a integração com mods externos no lado do cliente
- Exibe bosses de mods externos categorizados como Fase 1
- Monitora o status de conclusão desses bosses adicionais

**Formatação visual:**
- Utiliza ícones consistentes (✔/❌) para indicar status de conclusão
- Aplica esquema de cores para destacar diferentes tipos de informação
- Organiza o conteúdo em seções claramente delimitadas

##### `Phase1GoalsSection`
**Arquivo:** `net.mirai.dimtr.client.gui.sections.Phase1GoalsSection`

Implementação da interface `HUDSection` focada nos objetivos de eliminação de mobs da Fase 1 (Overworld).

**Funcionalidades principais:**
- Exibe contadores detalhados de mobs eliminados na Fase 1
- Organiza os mobs em categorias: comuns, especiais e mobs-objetivo
- Mostra progresso atual versus requisitos configurados para cada tipo de mob
- Fornece resumo estatístico do progresso geral de eliminação

**Categorias de mobs:**
- **Mobs Comuns**: Zombie, Skeleton, Stray, Husk, Spider, Creeper, Drowned
- **Mobs Especiais**: Enderman, Witch, Pillager, Vindicator, Bogged, Breeze
- **Mobs-Objetivo**: Ravager, Evoker (considerados mais desafiadores)

**Estatísticas e resumo:**
- Total de eliminações versus requisito total
- Número de tipos de mobs completos versus total de tipos
- Formatação visual que indica progresso parcial ou conclusão

**Comportamento contextual:**
- Verifica se o sistema de eliminação de mobs está habilitado no servidor
- Exibe mensagens informativas quando o sistema está desabilitado
- Só mostra contadores para mobs que possuem requisitos configurados (> 0)
- Acessibilidade baseada na configuração do servidor e status da Fase 1

**Formatação visual:**
- Utiliza ícones de status (✔/⚔) para indicar conclusão ou progresso
- Aplica código de cores para diferentes estados (vermelho, amarelo, verde)
- Organiza os contadores de forma clara e consistente
- Agrupa mobs logicamente por tipo e dificuldade

##### `Phase3MainSection`
**Arquivo:** `net.mirai.dimtr.client.gui.sections.Phase3MainSection`

Implementação da interface `HUDSection` dedicada à Fase 3 (End), representando o estágio final da progressão.

**Funcionalidades principais:**
- Exibe os objetivos especiais relacionados à dimensão do End
- Foca em bosses de mods externos categorizados para a Fase 3
- Apresenta o status de conclusão dos desafios finais
- Fornece informações sobre o desafio definitivo e recompensas

**Comportamento contextual:**
- Verifica se a Fase 2 está completa para permitir acesso
- Exibe mensagem explicativa quando a Fase 3 está bloqueada
- Verifica se a Fase 3 deve ser exibida (configuração do servidor)
- Mostra mensagem de conclusão quando todos os objetivos do End estão completos

**Integração com mods externos:**
- Inicializa a integração com mods externos no lado do cliente
- Foca exclusivamente em bosses de mods classificados como Fase 3 (End)
- Exibe mensagem informativa quando não há bosses do End configurados
- Monitora o status de conclusão dos bosses do End adicionados por mods

**Informações adicionais:**
- Apresenta uma seção sobre os desafios únicos da dimensão do End
- Explica a natureza da exploração e dos bosses poderosos do End
- Menciona a existência de recompensas definitivas para a conclusão
- Fornece contexto para o estágio final da progressão do mod

## UI

#### HUDComponentManager
**Arquivo:** `net.mirai.dimtr.client.ui.HUDComponentManager`

Sistema de gerenciamento de componentes de interface de usuário para exibição em tempo real (overlay) durante o jogo.

**Funcionalidades principais:**
- Gerencia componentes de HUD modulares e independentes
- Permite ativação/desativação dinâmica de componentes específicos
- Posiciona automaticamente os componentes na tela com base em suas configurações
- Fornece renderização flexível com suporte a diferentes posicionamentos

**Arquitetura:**
- **Padrão Singleton**: Garante uma única instância para gerenciar todos os componentes
- **Componentes Modulares**: Cada elemento do HUD é um componente independente
- **Posicionamento Flexível**: Suporte a diferentes posições na tela (TOP_LEFT, TOP_RIGHT, etc.)
- **Renderização Callback**: Componentes definem sua própria lógica de renderização

**Componentes Padrão:**
- **Phase Progress**: Exibe o status de progresso das fases 1 e 2
- **Party Status**: Mostra informações sobre o grupo atual, incluindo número de membros e multiplicador
- **Mob Kills**: Lista os 5 mobs mais eliminados pelo jogador
- **Special Objectives**: Exibe objetivos especiais como Elder Guardian, Raid, Wither e Warden

**Sistema de Renderização:**
- Renderiza apenas componentes ativos e visíveis
- Calcula posicionamento dinâmico com base no tamanho da tela
- Evita recursão durante o processo de renderização
- Aplica formatação visual consistente (fundos semi-transparentes, cores para status)

**Classe HUDComponent:**
- Armazena propriedades como ID, nome, dimensões e posição
- Mantém uma referência ao renderizador e condição de visibilidade
- Implementa interface fluente para fácil configuração
- Utiliza functional interfaces para comportamento flexível

**Integração:**
- Consome dados de `ClientProgressionData` para informações de progresso
- Utiliza dados de `ClientPartyData` para exibir informações sobre grupos
- Chamado pelo sistema de renderização do Minecraft para exibir overlays

#### ClientEventHandlers
**Arquivo:** `net.mirai.dimtr.client.ClientEventHandlers`

Classe responsável por capturar e gerenciar eventos do lado do cliente, focada principalmente no mapeamento de teclas e interações com a interface.

**Funcionalidades principais:**
- Define e registra atalhos de teclado específicos do mod
- Gerencia o comportamento quando esses atalhos são pressionados
- Responde a eventos de jogo para abrir interfaces de usuário

**Estrutura:**
- **Classe Principal**: Contém definição e registro de teclas
- **Classe Aninhada**: `ClientGameEventHandlers` para eventos de jogo

**Mapeamento de Teclas:**
- Define `OPEN_HUD_KEY` como atalho principal (tecla J por padrão)
- Registra este atalho no sistema de eventos do Minecraft
- Agrupa atalhos na categoria personalizada "key.categories.dimtr"

**Manipulação de Eventos:**
- `onRegisterKeyMappings`: Registra teclas personalizadas durante inicialização
- `onClientTick`: Verifica se teclas específicas foram pressionadas durante o ciclo de jogo
- Implementa o método `consumeClick()` para evitar múltiplas ativações

**Comportamentos Implementados:**
- Abre a tela `ProgressionHUDScreen` quando a tecla J é pressionida
- Verifica se não há outra tela aberta antes de abrir o HUD
- Utiliza o Minecraft singleton para interações com o cliente

**Anotações e Implementação:**
- Utiliza `@EventBusSubscriber` para registrar manipuladores de eventos
- Separa eventos de inicialização (MOD bus) dos eventos de jogo (GAME bus)
- Restringe a execução apenas ao lado do cliente usando `Dist.CLIENT`

**Integração:**
- Conecta-se ao sistema de eventos do NeoForge para capturar eventos do jogo
- Interage com o sistema de interfaces do Minecraft para exibir telas
- Serve como ponto de entrada para interação do usuário com o sistema de progressão

### Dados de Parties Client

#### `ClientPartyData`
**Arquivo:** `net.mirai.dimtr.client.ClientPartyData`

Classe singleton que gerencia informações sobre grupos/parties no lado do cliente.

**Funcionalidades principais:**
- Armazena dados sobre a party atual do jogador (ID, nome, membros, líder)
- Mantém informações sobre o progresso compartilhado entre membros do grupo
- Gerencia o multiplicador de requisitos baseado no tamanho do grupo
- Implementa sistema de cache para nomes de jogadores da party
- Suporta objetivos e fases customizadas compartilhadas

**Estrutura de dados:**
- **Dados da Party**: Identificador, nome, status público/privado, líder e membros
- **Progresso Compartilhado**: Estado de conclusão de objetivos principais compartilhados
- **Multiplicador**: Valor que determina o aumento de requisitos com base no tamanho do grupo
- **Cache de Nomes**: Mapeia UUIDs de jogadores para seus nomes para exibição consistente
- **Fases Customizadas**: Mapas para rastrear progresso compartilhado em conteúdo personalizado

**Funcionalidades específicas:**
- **Atualização de Dados**: Recebe e processa dados sincronizados do servidor
- **Gerenciamento de Cache**: Atualiza nomes de jogadores usando diferentes fontes de dados
- **Verificação de Estado**: Determina se o jogador está em uma party e seu papel
- **Acesso a Dados**: Fornece getters imutáveis para consumo seguro das informações
- **Proteção de Dados**: Implementa cópias defensivas para evitar modificações externas

**Comportamento contextual:**
- Detecta quando o jogador sai de uma party e limpa os dados correspondentes
- Mantém o cache de nomes mesmo quando jogadores estão offline/distantes
- Preserva apenas os dados relevantes no cache quando a composição do grupo muda

**Integração:**
- Conecta-se com o sistema de rede para receber atualizações do servidor
- Fornece dados para o sistema de UI (PartiesSection e HUD components)
- Influencia o comportamento de `ClientProgressionData` através do multiplicador de requisitos
- Fornece mecanismo para notificar atualizações de progresso do grupo