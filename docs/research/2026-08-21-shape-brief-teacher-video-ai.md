# Shape with AI KuiklyUI 题面与导师讲解报告（AI 版）

## 0. 文档用途

本文件面向后续规划 Agent、实现 Agent、Reviewer 和证据 Agent。它把 PDF、视频画面与转写稿归一为可引用的需求 ID、评分 ID、口头校准和未确认项。不得用本文件扩大正式题面；冲突时以 `docs/REQUIREMENTS.md` 为范围权威。

## 1. Source registry

| Source ID | Type | Location | Integrity | Role |
|---|---|---|---|---|
| SRC-PDF | official local PDF | 本地活动 PDF（未提交） | SHA-256 `13EBA084662D6C2F525B3E47E6BC1ACA1878C146BA546B09209223463674AD97` | formal task, weights, deliverables |
| SRC-VIDEO | mentor briefing video | 本地导师讲解视频（未提交） | SHA-256 `B0789367CD76B3A399B6FA3C90A1757C3B5A32515FD75E93FDCF9542BB180A49` | oral clarification + synchronized visuals |
| SRC-TXT | user-provided transcript | attached local TXT | SHA-256 `93C7A0D2FB653F880A1F75E19038A661F4A3278B1D4248B363DCF1894043CDA9` | audio semantic aid, not a substitute for video |

Analysis coverage: PDF pages 1-10; video full duration sampled every 30 seconds plus 16 full-resolution keyframes around task, scoring, delivery and Q&A segments.

## 2. Authority and interpretation rules

```text
formal task and formal score:
  SRC-PDF + docs/REQUIREMENTS.md

oral calibration:
  SRC-VIDEO synchronized with SRC-TXT

implementation truth:
  current source + tests + run evidence

not authoritative for Must:
  sample mockups, Issue #1477, archive/task1-v1, brainstorm ideas
```

Rules:

1. `FACT` = explicitly present in PDF/video or current repo.
2. `MENTOR_EMPHASIS` = repeated oral preference; it calibrates prioritization but does not alter the formal 40/25/25/10 weights.
3. `INFERENCE` = implementation strategy derived from facts; requires user confirmation if it changes scope.
4. `UNCONFIRMED` = mentor used uncertain language or promised follow-up; do not treat as contract.

## 3. Formal task contract

### 3.1 Task 1 requirements

| ID | Level | Requirement | Evidence target |
|---|---|---|---|
| T1-F01 | MUST | market list shows name, code, latest price, change amount, change percent | UI + fixture + test |
| T1-F02 | MUST | list scrolls | browser/device interaction |
| T1-F03 | MUST | correct stock opens correct detail | typed route test + interaction |
| T1-F04 | MUST | detail shows name, code, latest price, change percent, high, low, volume | UI + test |
| T1-AI01 | MUST, divergent form | detail contains visible AI analysis/interpretation | UI + demo |
| T1-AI02 | OPTIONAL FORM | buy/sell point, operation hint, trend, risk, signal or summary | chosen design contract |
| T1-AI03 | OPTIONAL FORM | card, tag, prompt area or text analysis area | chosen design contract |

### 3.2 Task 2 requirements

| ID | Level | Requirement | Evidence target |
|---|---|---|---|
| T2-F01 | MUST | input question | UI test/interaction |
| T2-F02 | MUST | send message | state test/interaction |
| T2-F03 | MUST | display conversation history | state + long-session evidence |
| T2-R01 | MUST | render Markdown | renderer test + UI |
| T2-R02 | MUST | render at least one non-Markdown stock/index/market business form | typed block + UI |
| T2-D01 | MUST | at least one chat result opens a detail page | route test + demo |
| T2-D02 | MUST | detail has base quote + trend region + summary or AI interpretation | UI + demo |

## 4. Formal rubric

| ID | Weight | Formal criteria | Required project artifact |
|---|---:|---|---|
| R-FUNC | 40 | page loop, requirement coverage, state completeness | requirements matrix + tests + interaction evidence |
| R-ENG | 25 | layering, maintainability, extensibility, conventions; incubatable common component | component contract + callers/fixtures + tests + architecture note |
| R-AI | 25 | natural business integration, demonstrable ability, innovative AI scene | AI carrier brief + interaction storyboard + demo evidence |
| R-BONUS | 10 | platform coverage, real API, experience optimization | separately verified evidence; never inferred |

Formal delivery:

| ID | Requirement |
|---|---|
| DEL-CODE | independent repository; complete, compilable/runnable Kuikly project; clear naming/comments |
| DEL-DOC | README/project doc: introduction, stack, architecture/directory, highlight explanation |
| DEL-VIDEO | prototype demo video: full chain, interaction flow, key operations, AI effect |

## 5. Mentor emphasis records

| ID | Time(s) | Visual anchor | Normalized statement | Confidence |
|---|---|---|---|---|
| M-DIVERGE | 00:01-00:55, 03:07, 07:09, 08:33 | task overview | task is intentionally divergent; sample is not the answer | high |
| M-BASELINE | 06:09, 07:48, 23:45 | rubric/overview | base features earn base score but have low differentiation | high |
| M-COMPONENT | 06:30-08:10, 19:22-20:01 | rubric + Task 1 mockup | wants reusable, incubatable business components | high |
| M-AI-CARRIER | 15:44-18:49 | rubric then Task 1 AI card | focus is the UI carrier and interaction for AI information, not model/content quality | high |
| M-MOCK-OK | 18:27-18:49, 22:50 | Task 1 mockup | data source/model strength is secondary; Mock is acceptable | high |
| M-NARROW-DEEP | 19:22-20:40 | Task 1 mockup | avoid grand scope; grow functionality incrementally and complete it | high |
| M-TREND-AI | 21:40-22:50 | trend line + AI card | AI signals can be integrated into trend visuals; example only | medium-high |
| M-RUNNABLE | 10:13-10:55 | deliverables slide | submitted code must actually run | high |
| M-DOC-DEMO | 10:55-11:33 | deliverables slide | doc explains highlights; video helps evaluation | high |
| M-BONUS-LATER | 08:53-09:39 | rubric slide | platforms, real data/API and UI polish are bonuses; do not overinvest | high |

## 6. Unconfirmed records

| ID | Time | Claim | Required action |
|---|---|---|---|
| U-DEADLINE | 11:50, 15:05 | submission may be Sep 14 | confirm with organizer |
| U-TRACKS | PDF delivery slide; 26:12-29:25 | delivery slide mentions Task 1 + Task 2 core code together, while Q&A says Tasks are likely scored separately and doing both is optional if capacity allows | maintain separate scorecards; confirm whether one or both Tasks must be delivered before submission strategy |
| U-TEAM | 26:40-28:03 | collaboration may be allowed while award may be individual | confirm ownership/submission rules |
| U-AWARDS | 30:14-30:52 | roughly three awards and mixed allocation | do not optimize scope based on this statement |

## 7. Derived implementation strategy

| Strategy ID | Type | Statement | User confirmation needed? |
|---|---|---|---|
| S-01 | INFERENCE | close the Must vertical slice before innovation work | no; consistent with current workflow |
| S-02 | INFERENCE | spend most discretionary effort on R-ENG + R-AI, not extra ordinary pages | yes when choosing feature scope |
| S-03 | INFERENCE | select exactly one primary AI carrier per Task for first judged version | yes |
| S-04 | INFERENCE | require one component incubation contract with at least two callers or data variants | yes |
| S-05 | INFERENCE | allow a minimal trend/AI-overlay component without making generic chart/K-line a Must | yes |
| S-06 | INFERENCE | keep Mock-first; real API/platform coverage starts only after core score gates pass | no; already adopted |
| S-07 | INFERENCE | maintain Task-specific final demo and README narratives | yes if both Tasks are submitted |

## 8. Candidate innovation inventory

### Task 1 candidates

| Candidate | AI carrier | Reusable component | Score leverage | Cost | Scope warning |
|---|---|---|---|---|---|
| Explainable Insight Surface | conclusion + reasons + risk + freshness | `MarketInsightSurface` | R-ENG + R-AI | medium | recommended baseline candidate |
| Trend Signal Overlay | signals anchored to trend points | `TrendSignalLayer` | R-AI + demo | medium | not generic K-line platform |
| Scenario Lens | optimistic/base/risk scenario switching | `ScenarioSwitcher` + insight panel | R-AI | medium | Mock semantics must be clear |
| Risk Timeline | event/risk markers with expansion | `EventMarkerTrack` | R-ENG + R-AI | medium-high | avoid claiming predictive truth |
| Accessibility Summary | textual trend/risk alternative | `AccessibleMarketSummary` | R-ENG + bonus | low-medium | should complement, not replace visual |

### Task 2 candidates

| Candidate | AI carrier | Reusable component | Score leverage | Cost | Scope warning |
|---|---|---|---|---|---|
| Typed Result Canvas | ordered Markdown + business blocks | `ChatBlockRenderer` | R-ENG + R-AI | medium | recommended baseline candidate |
| Compare Card | two entities + deltas + rationale | `MarketCompareCard` | R-AI | medium | requires consistent fixtures |
| Follow-up Chips | contextual actions/questions | `SuggestionChipGroup` | R-AI + UX | low | must update conversation meaningfully |
| Claim/Evidence Pair | AI claim with expandable evidence | `EvidenceDisclosure` | R-AI + trust | medium | do not imply real research if Mock |
| Detail Continuation | card → detail → return to same chat | route/session contract | R-FUNC + R-ENG | medium | state restoration must be tested |

## 9. Score-aligned gate schema

Each Task PLAN must define:

```yaml
rubric_mapping:
  R-FUNC: {target: null, visible_result: null, evidence: []}
  R-ENG: {target: null, visible_result: null, evidence: []}
  R-AI: {target: null, visible_result: null, evidence: []}
  R-BONUS: {target: null, visible_result: null, evidence: []}
innovation_contract:
  candidates: []
  selected_by_user: null | name
  ai_carrier: null | name
  reusable_component: null | name
  reuse_proof: null | description
demo_contract:
  user_story: string
  start_state: string
  key_actions: []
  visible_outcome: string
  failure_or_recovery: string
scope_control:
  non_goals: []
  cut_line: string
handoff_contract:
  base_sha: string
  branch: feature/task1-topic | feature/task2-topic
  writer: OpenCode | Codex-approved-fallback
  allowed_paths: []
  verification_commands: []
```

A Task can enter CODE only after the user confirms the PLAN. Its delivery then advances in order through CODE, TESTS and LEARNING. The submission candidate additionally requires all four rubric rows and all three delivery artifacts to have direct evidence.

## 10. Agent execution constraints

1. Do not copy archived implementation or count archived evidence.
2. Do not change formal Must based on mockup imagery or brainstorming.
3. Do not use real LLM/API/platform claims without explicit verification.
4. Do not describe Mock analysis as investment advice or prediction.
5. Do not treat compilation, HTTP 200, browser visual, APK build and device run as equivalent.
6. Do not call a split file structure a reusable component without a stable contract and reuse proof.
7. Do not mark an innovation complete unless it is visible in the runnable demo.
8. End each task handoff with a concrete next step and external-operation statement.
