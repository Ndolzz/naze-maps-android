# NAZE MAPS — Project Constitution (SDD-010)

- ID: SDD-010
- Title: Project Constitution
- Status: APPROVED

Aturan dasar pengembangan Naze Maps. Berlaku untuk semua kontributor (manusia dan AI agent).

## Prinsip

1. **Specification First** — Tidak ada fitur/behavior baru tanpa specification di docs/sdd/ (status minimal PROPOSED).
2. **Architecture First** — Perubahan besar (layer baru, module baru, ganti library) wajib ADR di docs/sdd/decisions/.
3. **No Blind Refactoring** — Dilarang refactor tanpa memahami dependency & impact. Konsultasikan 00-current-baseline.md dan technical-debt.md dulu.
4. **Backward Compatibility** — Feature yang jalan (baseline: peta, lokasi, compass, search, routing, favorites, history, distance, settings, satellite, share) tidak boleh rusak tanpa alasan terdokumentasi + approval.
5. **Test Before Refactor** — Area tanpa test yang akan diubah wajib characterization test dulu (jika memungkinkan secara praktis; jika tidak, dicatat di ADR/task).
6. **Small Changes** — Satu commit = satu responsibility. Dilarang mencampur fix bug + refactor + fitur dalam satu perubahan.
7. **Verify Every Change** — Setiap perubahan diverifikasi: compile (CI), unit test, test relevan, regression check manual jika berdampak UI/behavior, review git diff.

## Urutan kerja wajib

Requirement → Specification → Architecture (jika perlu) → Implementation → Test → Verification → Documentation update → Commit.

## Definition of Done

- [ ] Specification tersedia & sesuai
- [ ] Scope jelas
- [ ] Impact dianalisis
- [ ] Implementation selesai (minimum change)
- [ ] Build berhasil (CI hijau)
- [ ] Test relevan lulus
- [ ] Regression check dilakukan
- [ ] Dokumentasi diperbarui
- [ ] Tidak ada perubahan tidak terkait di diff
- [ ] Git diff diperiksa

## Change Control

Change Request → specification update → impact analysis → implementation plan → implementation → testing → verification → documentation → commit. Dilarang mengubah specification setelah coding hanya demi membenarkan implementasi; jika mismatch, laporkan.

## Git Safety

Sebelum menyentuh kode: pahami `git status`, `git branch`, `git log`. Tidak boleh `git reset --hard`/`git clean -fd` tanpa instruksi eksplisit. Fase besar harus bisa dirollback via commit jelas.

## Anti-chaos

Jangan: rewrite massal, ganti library karena tren, abstraction spekulatif, interface demi pola, memindah file tanpa tujuan, fix bug sambil refactor besar, ubah behavior tanpa specification, hapus kode yang belum dipahami, dependency baru tanpa alasan, premature optimization.

**Motto: Small change. Clear specification. Measurable verification.**
