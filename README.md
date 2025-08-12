# PandaPass

PandaPass is a lightweight, clean, and minimalistic open-source password manager built with **Java 21** and **JavaFX**.

- **Lightweight** — Runs without heavy dependencies, cloud services, or large installers; stores everything locally.
- **Clean** — Distraction-free interface with intuitive navigation.
- **Minimalistic** — Only the essential features you need to manage passwords securely — no bloat.

It provides a secure vault for storing your login credentials **locally on your machine**, with asynchronous features for a smooth and responsive user experience.

---

## Features

- **Secure Vault** — Local encrypted storage of credentials
- **Password Breach Check** — Asynchronous check using the *Have I Been Pwned* API
- **Add, Edit, Delete** — Manage your entries easily
- **Clipboard Copy** — Copy passwords with automatic timeout-based clearing
- **TOTP** – Used for 2FA.
- **Password Visibility Toggle** — Inspect passwords only when needed
- **Safe Deletion** — Slider confirmation for irreversible actions
- **Search Functionality** — Quickly find services and usernames
- **Category Management** — Organize entries by category
- **Modern UI** — JavaFX MVVM architecture, dark theme, icon-enhanced entry cells

---

## 📦 Installation & Setup
None yet.


## Technology Stack
- Java 21
- JavaFX (UI)
- MVVM pattern
- PostgreSQL

## Notes
- All data is stored locally and encrypted before being saved.
- Clipboard content is cleared automatically after a short timeout.
- Password breach check uses Have I Been Pwned without sending your full password.

## Roadmap
- Export/Import encrypted vault files
- Custom encryption key support
- Cross-platform build packages
- Password Strength Indicator
- Multi-language support

## License
This project is licensed under the MIT License.