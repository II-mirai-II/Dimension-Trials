# 📋 Dimension Trials: Guia Completo

*Um guia completo sobre o funcionamento interno do mod Dimension Trials para jogadores e administradores de servidores*

## 🌟 Visão Geral

Dimension Trials é um mod para Minecraft que implementa um sistema de progressão em fases, com acesso a dimensões bloqueado até que você complete desafios específicos. O mod inclui:

- **Sistema de Progressão por Fases**: Avance através de diferentes fases completando objetivos específicos
- **Sistema de Parties Colaborativas**: Forme grupos para progredir coletivamente e reduzir requisitos individuais
- **Interface de HUD Interativa**: Visualize seu progresso de forma intuitiva
- **Sistema de Configuração Abrangente**: Personalize todos os aspectos do mod
- **Integração com Outros Mods**: Suporte automático para Mowzie's Mobs, L_Ender's Cataclysm e mais

## 🎮 Mecânicas Principais

### 1. Sistema de Progressão por Fases

#### Fase 1: Overworld para Nether
- **Mobs do Overworld**: Elimine 16 tipos de mobs (zombies, skeletons, spiders, etc.)
- **Objetivos Especiais**: Derrote o Elder Guardian, vença uma Raid, conquiste o Trial Vault
- **Recompensa**: Acesso ao Nether, multiplicador de saúde/dano 1.5x, XP 1.25x

#### Fase 2: Nether para End
- **Mobs do Nether**: Elimine 7 tipos de mobs (blaze, wither skeleton, ghast, etc.)
- **Objetivos Especiais**: Derrote o Wither e o Warden
- **Recompensa**: Acesso ao End, multiplicador de saúde/dano 2.0x, XP 1.5x

#### Fases Customizadas
- Defina fases adicionais para modpacks usando arquivos JSON
- Adicione requisitos para dimensões de outros mods (Twilight Forest, Aether, etc.)
- Configure multiplicadores de dificuldade para cada fase

### 2. Sistema de Parties

#### Mecânicas de Party
- **Criação de Party**: Use `/party create` para formar um grupo
- **Compartilhamento de Progresso**: Todos contribuem para os mesmos objetivos
- **Scaling Dinâmico**: Requisitos ajustados automaticamente pelo tamanho do grupo
- **Fórmula de Redução**: `Requisito Individual = Requisito Base ÷ Tamanho da Party`

#### Impacto no Jogo
- **Redução de Grinding**: Menos mobs para matar individualmente
- **Multiplicadores de Party**: Aumento na dificuldade de mobs proporcional ao tamanho
- **Acesso Compartilhado**: Quando a party completa uma fase, todos ganham acesso
- **Limite de Party**: Máximo de 10 jogadores por party (configurável)

### 3. Integração com Mods Externos

#### Mowzie's Mobs
- **Bosses Adicionados**: Ferrous Wroughtnaut, Frostmaw, Umvuthi, Naga, Sculptor
- **Todos na Fase 1**: Categorizados como bosses do Overworld

#### L_Ender's Cataclysm
- **Fase 1 (Overworld)**: Ancient Remnant, Leviathan
- **Fase 2 (Nether)**: Netherite Monstrosity, Ignis, Harbinger, Maledictus
- **Fase 3 (End)**: Ender Guardian, Ender Golem

#### Outros Mods Suportados
- **Twilight Forest**: Dimensão bloqueada até completar requisitos
- **The Aether**: Dimensão bloqueada até completar requisitos

### 4. Sistema de Multiplicadores

#### Multiplicadores de Fases
- **Fase 1 Completa**: Mobs +50% de vida e dano, +25% de XP
- **Fase 2 Completa**: Mobs +100% de vida e dano, +50% de XP
- **Fases Customizadas**: Multiplicadores definidos na configuração

#### Multiplicadores de Party
- **Fórmula**: 1.0 + ((membros - 1) * 0.5), limitado a 3.0x
- **2 jogadores**: 1.5x multiplicador
- **3 jogadores**: 2.0x multiplicador
- **4 jogadores**: 2.5x multiplicador
- **5+ jogadores**: 3.0x multiplicador (máximo)

### 5. Sistema de Backup e Recuperação

- **Backups Automáticos**: A cada 30 minutos (configurável)
- **Backups Manuais**: Use `/dimtr backup create` para criar um backup
- **Restauração**: Use `/dimtr backup restore <id>` para recuperar dados
- **Rotação Automática**: Manutenção de até 10 backups (configurável)

## 📋 Comandos Essenciais

### Comandos de Party
```
/party create              # Criar nova party
/party invite <player>     # Convidar jogador
/party accept              # Aceitar convite
/party leave               # Sair da party
/party info                # Ver detalhes da party
/party kick <player>       # Remover membro (apenas líder)
/party promote <player>    # Transferir liderança
/party disband             # Dissolver a party
```

### Comandos de Progressão
```
/dimtr status              # Verificar sua progressão
/dimtr player <player> status  # Verificar progressão de outro jogador (OP)
/dimtr complete phase1     # Completar Fase 1 (OP)
/dimtr complete phase2     # Completar Fase 2 (OP)
```

### Comandos de Backup
```
/dimtr backup create       # Criar backup manual
/dimtr backup list         # Listar backups disponíveis
/dimtr backup restore <id> # Restaurar de um backup (OP)
```

## ⚙️ Configuração Rápida

As configurações principais estão em `config/dimtr-server.toml`:

### Requisitos de Mobs
```toml
# Fase 1 - Mobs Comuns do Overworld
reqZombieKills = 50
reqSkeletonKills = 40
reqSpiderKills = 30

# Fase 2 - Mobs do Nether
reqBlazeKills = 20
reqWitherSkeletonKills = 15
reqGhastKills = 10
```

### Objetivos Especiais
```toml
# Fase 1
reqElderGuardian = true
reqRaid = true
reqTrialVaultAdv = true

# Fase 2
reqWither = true
reqWarden = true
```

### Multiplicadores
```toml
enableMultipliers = true
phase1Multiplier = 1.5
phase2Multiplier = 2.0
enableXpMultiplier = true
xpMultiplierPhase1 = 1.25
xpMultiplierPhase2 = 1.5
```

### Sistema de Party
```toml
enablePartySystem = true
maxPartySize = 10
partyInviteTimeout = 300
```

### Backups
```toml
enableAutomaticBackups = true
backupInterval = 30
maxBackupCount = 10
```

## 🛠️ Problemas Comuns e Soluções

### Problemas de Progressão
- **Kills não contando**: Verifique se `enableMobKillsPhase1/2` está true
- **Não consigo acessar o Nether/End**: Use `/dimtr status` para verificar progresso

### Problemas de Party
- **Convites não funcionam**: Verifique o timeout de convite (padrão: 5 minutos)
- **Progresso não sincronizando**: Verifique se todos estão na mesma versão do mod

### Problemas de Configuração
- **Configuração não carrega**: Reinicie o servidor após alterações
- **Requisitos muito altos/baixos**: Ajuste no arquivo dimtr-server.toml

---

**Para informações mais detalhadas, consulte:**
- [Guia do Sistema de Party](PARTY_SYSTEM.md)
- [Requisitos Customizados](CUSTOM_REQUIREMENTS.md)
- [Configuração Completa](CONFIGURATION.md)
