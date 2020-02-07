### Agent Util

[TOC]

#### 1.Quick Start
```bash
bash install.sh
cd /usr/local/bin/agent
bash agent.sh
```

#### 2. Feature

- Redefine class
- Start jmx port when forgot add JVM options
- Execute code to see ram data by groovy script engine, you can use Java or Groovy syntax

#### 3. Usage

##### 3.1 Redefine class

```bash
bash hotswap.sh processId hotswapClassDir
```

##### 3.2 Start jmx

```bash
bash jmxStart.sh processId
```

##### 3.3 Query ram data

```bash
bash gs.sh processId scriptFileName
```

#### 4. TODO

- add dynamic code monitor like arthas

