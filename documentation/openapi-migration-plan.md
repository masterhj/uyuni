# OpenAPI API-documentation migration — plan for the remaining handlers

Status: proposal, 2026-08-09. Base: `open-api` @ `1560fa3c113` (includes #12383 + #12384).

## Where we are

12 of 67 documented namespaces have been migrated from the `@apidoc.*` Javadoc doclet to
OpenAPI contract interfaces:

`access`, `admin.configuration`, `admin.payg`, `admin.ssh`, `api`, `channel.access`,
`distchannel`, `kickstart.snippet`, `packages.search`, `preferences.locale`, `saltkey`,
`subscriptionmatching.pinnedsubscription`

55 namespaces / ~888 documented methods remain.

## The plan, in order

1. **Migrate everything the current architecture can already express**, in batches, with no
   parser or generator changes at all.
2. **Then** tackle the architecture gaps that phase 1 proves are genuinely required, one gap
   per PR.

Doing it in this order means every architecture change is justified by handlers that are
blocked on it and by nothing else — instead of being designed speculatively.

To make phase 1 concrete I classified all 55 remaining namespaces up front rather than
discovering blockers one handler at a time. Method and results below.

## How reachability was measured

The legacy `@apidoc.*` tags are Velocity macros rendered by
`java/webapp/src/apidoc/{asciidoc,docbook}/macros.txt`, so the tags are an exact description of
the output the migration has to reproduce. The classifier parses every `*Handler.java` under
`com/redhat/rhn/frontend/xmlrpc` **and** `com/suse/manager/xmlrpc`, inlines the 129 indexed
`$XSerializer` references so nested shapes become visible, and replays the `$listlevel` counter
to recover the nesting each macro produces.

It was validated against the 11 namespaces already migrated on the throwaway
`wip-openapi-migration` branch, where the outcome of every one is known from real generated-file
diffs. **It reproduced all 11 verdicts exactly, with no false "clean".**

Two shapes deliberately do *not* count as blockers, because they were tested and turned out to
be reachable by authoring alone:

- **Additive overloads** (`f(a)` and `f(a, b)`): declare one method with the union of
  parameters and mark the extras `@Parameter(required = false)`. Already shipped in
  `distchannel.listMapsForOrg` and `admin.payg.create`.
- **Dates in request parameters:** `@LegacyDocResponse(type = "dateTime.iso8601")` on the
  request getter. The existing annotation already covers it.

## Phase 1 — reachable with zero architecture change

Only **three** namespaces are reachable, and all three are now written and verified:

| namespace | methods | status |
|---|---:|---|
| `image.profile` | 10 | AsciiDoc identical to legacy, DocBook text identical, contract test 10/10 |
| `image.store` | 6 | AsciiDoc identical to legacy, DocBook text identical, contract test 6/6 |
| `kickstart.profile.software` | 6 | contract test 6/6, but **not identical** — two divergences, below |

Four more namespaces classify clean but are not available:

- `taskomatic`, `taskomatic.org` — marked `@apidoc.ignore`, they are not in the published docs.
- `auth` — clean by shape, but `HttpApiRegistry` drops `@ApiIgnore(ApiType.HTTP)` methods, so it
  cannot be served over HTTP. Registry-level, out of scope here.
- `system.monitoring` (1 method) — needs a probe first, see P1 below.

**This is the headline result: exhausting the current architecture yields three handlers, not
fifty.** Every other namespace contains at least one shape the generator cannot reproduce. That
does not change the order of work, but it does mean phase 1 is essentially finished and the
architecture gaps are the whole remaining project.

### Phase 1 PRs

- **PR A — `image.profile` + `image.store`.** Both byte-identical to legacy after normalization,
  which is a stricter bar than the merged baseline clears: `saltkey` currently emits description
  text the doclet never produced, and `admin.payg` renders its two `create` overload variants in
  reversed order.
- **PR B — `kickstart.profile.software` alone.** `kickstart.tree` was dropped, see below.

`kickstart.profile.software` has two divergences in `setSoftwareList`, both unavoidable under
the current architecture:

1. The two overload variants render in reversed order versus legacy, in the index and the body.
   Merged `admin.payg` has exactly the same divergence.
2. Legacy uses a **different article per overload** — *"the list of package names"* on the 3-arg
   variant and *"a list of package names"* on the 5-arg one. An additive overload is a single
   schema property carrying a single description, so one of the two variants must differ by that
   word. Declaring the overloads separately does not help: paths are keyed on the method name,
   so they would overwrite each other (G3).

Also worth recording: legacy declares `getSoftwareDetails` properties as `string` while the
handler returns `Map<String, Boolean>`. The contract reproduces the documented type, so the
contract test asserts strings.

### `kickstart.tree` is blocked (correction to an earlier version of this plan)

An earlier revision listed `kickstart.tree` as clean. That was a classifier bug: serializers were
inlined *before* nesting levels were walked, which hid nested references.
`KickstartTreeDetailSerializer` embeds `$KickstartInstallTypeSerializer` inside its struct, and
`OpenApiToAsciidocParser.structPropertyType` (~line 405) falls back to `"string"` for `$ref`
properties, so the nested struct is lost. That is **G9**, which is not authorable.

The classifier now distinguishes a serializer nested inside a **struct** (a `$ref` property, G9)
from one inside `#return_array_begin()` (the ordinary array-of-refs form, which renders
correctly). It reproduces all 12 known verdicts, including the three handlers proven by
real-file diff.

### Open probe (P1) — non-string array element types

`OpenApiToAsciidocParser:234` hardcodes `"[.array]#string array#"`, while legacy renders the
element type (`#atype($type)` → `[.array]#$type array#`). 24 of the remaining namespaces contain
an array whose element is not a string, `system.monitoring` being the smallest at one method
(`#array_single("int", "sids")`).

`parameterType()` consults `legacyDocType()` *before* reaching that hardcoded branch, so this may
be authorable via `@LegacyDocResponse(type = ...)` with no parser change — the same route that
solved request-side dates. **`system.monitoring` is the cheapest place to find out**, and the
answer decides whether P1 belongs in phase 1 or phase 2.

## Per-PR checklist

This is the workflow the six merged handler PRs used; all six drew zero review comments.

1. Contract interface + `implements` on the handler + registration in `OpenApiConfig`.
2. **Contract test with real inputs**, modelled on `AdminPaygHandlerContractTest` — every method
   plus each overload variant, using concrete objects rather than bare interfaces so the
   serializer actually emits the documented properties.
3. `ApiDocumentationCompatibilityTest`, both AsciiDoc and DocBook.
4. **A real-file normalized diff of generated versus legacy AsciiDoc.** This step is not
   optional: the compatibility test's `DocItem` carries no description field, so any divergence
   living in description text is invisible to it. That is how a handler that was actually broken
   passed the compatibility test earlier in this project.
5. Checkstyle on every touched file.

Three divergences are pre-existing, present on every already-merged handler, and should not be
chased: the anchor `_` versus `.`, the missing `- ` on `#return_int_success()`, and `* *` versus
`*` on a bare struct return.

## Phase 2 — the architecture gaps

Each of these is a separate PR that generalizes existing code rather than adding a parallel
path. Namespaces blocked, and what each unlocks cumulatively when applied in this order:

| gap | what breaks | ns blocked | cumulative clean |
|---|---|---:|---:|
| **G1** | int return with its own description is ignored | 17 | 8 |
| **G6** | array property inside a struct collapses to `string` | 22 | 12 |
| **G5** | date in a *response* property collapses to `string` | 21 | 18 |
| **G7** | array-return item struct rendered one level too shallow | 16 | 23 |
| **G10** | `#options()` / `#item()` blocks not rendered | 18 | 31 |
| **G9** | struct-typed property renders as `string` | 13 | 38 |
| **G3** | alternative overloads collapse to one operation | 12 | 45 |
| **G4** | array parameter items not expanded | 8 | 50 |
| **G8** | legacy return types with no OpenAPI counterpart | 5 | 55 |

Notes that affect sequencing:

- **G1 and the DocBook struct-parameter nesting fix are already written** on the
  `wip-openapi-migration` branch and only need porting. G1 should go first: it is done, and it is
  a prerequisite for 17 namespaces.
- **G10 is DocBook-only.** The AsciiDoc compatibility check skips `#options()` items because they
  carry no `[.type]#` marker, so an options-only namespace passes AsciiDoc and fails DocBook.
  Record `@Schema(allowableValues = {...})` while authoring regardless — it is the correct
  OpenAPI expression even before a parser renders it.
- **G5 response-side dates and G8 share one root cause:** `applyLegacyDocTypes` is called only
  with `apiDoc.requestClass()`, so `@LegacyDocResponse(type = ...)` never reaches a response
  property. This is why request-side dates are authorable and response-side ones are not. The fix
  is widening an existing call site, not a new annotation.
- **G7 needs an authoring signal, not only a parser fix.** Legacy renders an array return at a
  different level depending on whether the source used `$SomeSerializer` (level 1, what the
  parser emits today, and correct) or an inline `#struct_begin` (level 2). Both compile to the
  same OpenAPI shape, so the distinction cannot be recovered from the spec.
- **G3** must key paths on the full signature rather than the method name
  (`UyuniSwaggerReader.registerOperationOnPath` currently does `buildPath(namespace,
  method.getName())`, so the second overload silently overwrites the first). `system` alone
  accounts for 69 of the 119 affected variants.

The four largest namespaces — `system` (221 methods, 9 blockers), `channel.software` (79, 8),
`kickstart.profile` (34, 5), `configchannel` (29, 5) — are blocked by nearly every gap, so they
are the natural last milestone rather than an early target.

## Blocked namespaces by gap

- **G1** actionchain, activationkey, ansible, channel.software, configchannel, kickstart.profile, org, org.trusts, recurring.custom, recurring.highstate, recurring.playbook, system, system.appstreams, system.scap, user, user.external
- **G3** actionchain, activationkey, ansible, channel.software, configchannel, org, proxy, system, system.provisioning.powermanagement, system.provisioning.snapshot, system.scap, systemgroup
- **G4** actionchain, activationkey, channel.software, errata, kickstart.profile, system, system.appstreams
- **G5** activationkey, channel.appstreams, channel.software, configchannel, contentmanagement, errata, kickstart, kickstart.filepreservation, org.trusts, packages, proxy, recurring, schedule, system, system.config, system.custominfo, system.provisioning.snapshot, system.scap, system.search, systemgroup
- **G6** activationkey, audit, channel.appstreams, channel.software, errata, kickstart, kickstart.filepreservation, kickstart.profile, kickstart.profile.keys, kickstart.profile.system, packages, packages.provider, recurring, sync.content, system, system.appstreams, system.provisioning.snapshot, systemgroup, user.external, user.notifications
- **G7** actionchain, activationkey, admin.monitoring, channel, channel.org, channel.software, contentmanagement, errata, image, kickstart.keys, kickstart.profile.system, org.trusts, packages, system, user
- **G8** ansible, channel.software, kickstart.profile, system, virtualhostmanager
- **G9** activationkey, ansible, configchannel, contentmanagement, formula, image, image.delta, kickstart.tree, maintenance, system, system.config, systemgroup, virtualhostmanager
- **G10** activationkey, audit, channel.software, configchannel, image, kickstart.profile, kickstart.profile.keys, kickstart.profile.system, packages, recurring, recurring.custom, recurring.highstate, system, system.config, system.provisioning.snapshot, systemgroup, user

## Open question for review

Phase 1 is three handlers and is already written. Everything after it requires architecture work,
so the pace of the rest of the migration is set entirely by how quickly the gap PRs can be
reviewed and merged. Is the ordering in the phase 2 table the right one — G1 first because it is
already written, then by number of namespaces unlocked — or should the largest namespaces
(`system` at 221 methods, `channel.software` at 79) drive the order instead, which would mean
starting with G3?

A second question follows from `kickstart.profile.software`: where legacy documentation is
internally inconsistent (a different article per overload) or simply wrong about a type
(`getSoftwareDetails` declares `string` for booleans), should the migration reproduce it exactly,
or is correcting it acceptable? Reproducing it is what the compatibility test rewards, but it
carries known errors forward.
