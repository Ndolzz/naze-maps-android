# ADR-002: Target Architecture

## Context
Baseline ADR-001; kebutuhan: stabil, testable, mudah dikembangkan; anti-over-engineering untuk app skala ini.

## Problem
Bagaimana berevolusi tanpa rewrite dan tanpa regresi pada 12 fitur berjalan.

## Decision
Pertahankan single-module dan repository pattern. Evolusi bertahap: (1) constructor injection manual repo ke ViewModel (bukan Hilt dulu); (2) pecah MapUiState per feature screen-state; (3) UI hanya bicara ke ViewModel — larangan UI membuat repo; (4) domain util murni tetap di utils/ (tanpa androidx import); (5) boundary SDK: MapLibre hanya lewat map/, Play Services hanya lewat location/, Room hanya lewat favorites/history. Multi-module dan UseCase-class massal TIDAK diadopsi sekarang.

## Alternatives Considered
Clean Architecture multi-module + Hilt (ditolak: kompleksitas tidak proporsional); MVI (ditolak: rewrite besar); tetap status quo (ditolak: untestable & god-object).

## Consequences
Migration plan kecil-kecil (TASK-001..012); setiap boundary dikerjakan setelah characterization test; tetap fleksibel adopt Hilt nanti bila 3+ consumer.

## Status
PROPOSED
