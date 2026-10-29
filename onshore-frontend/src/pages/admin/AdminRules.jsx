import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { EmptyState, ErrorBanner, PageHeader } from "../../components/ui/Feedback";
import { Button } from "../../components/ui/Button";
import { Card, CardTitle } from "../../components/ui/Card";
import { Badge } from "../../components/ui/Badge";
import { Field, Input, Select } from "../../components/ui/Field";
import { useAction } from "../../hooks/useAction";
import { deleteRule, fetchAllPartners, fetchRules, fetchSettings, saveRule, saveSettings } from "../../Redux/admin/adminSlice";
import { fetchActivities } from "../../Redux/catalog/catalogSlice";

const SCOPES = [
  { value: "SESSIONS_PER_DAY", label: "Séances par jour" },
  { value: "PARTNER_PER_MONTH", label: "Séances par mois chez un partenaire" },
  { value: "ACTIVITY_PER_MONTH", label: "Séances par mois sur une activité" },
];

function SettingsForm({ settings }) {
  const { run, busy } = useAction();
  const [form, setForm] = useState(settings);
  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });
  const submit = (event) => {
    event.preventDefault();
    run(
      saveSettings({
        checkinBeforeMinutes: Number(form.checkinBeforeMinutes),
        checkinAfterMinutes: Number(form.checkinAfterMinutes),
        cancellationHours: Number(form.cancellationHours),
      }),
      "Paramètres enregistrés",
    );
  };
  return (
    <form onSubmit={submit} className="mt-3 grid gap-2 md:grid-cols-4">
      <Field label="Check-in ouvert avant le cours (min)"><Input type="number" min="0" value={form.checkinBeforeMinutes} onChange={set("checkinBeforeMinutes")} /></Field>
      <Field label="Check-in ouvert après le début (min)"><Input type="number" min="0" value={form.checkinAfterMinutes} onChange={set("checkinAfterMinutes")} /></Field>
      <Field label="Annulation remboursée jusqu'à (h avant)"><Input type="number" min="0" value={form.cancellationHours} onChange={set("cancellationHours")} /></Field>
      <Button type="submit" disabled={busy} className="self-end">Enregistrer</Button>
    </form>
  );
}

export default function AdminRules() {
  const dispatch = useDispatch();
  const { rules, settings, partners, error } = useSelector((s) => s.admin);
  const activities = useSelector((s) => s.catalog.activities);
  const { run, busy } = useAction();
  const [form, setForm] = useState({ scope: "SESSIONS_PER_DAY", partnerId: "", activityId: "", maxUses: "" });

  useEffect(() => {
    dispatch(fetchRules());
    dispatch(fetchSettings());
    dispatch(fetchAllPartners());
    dispatch(fetchActivities({}));
  }, [dispatch]);

  const describe = (r) => {
    const label = SCOPES.find((s) => s.value === r.scope)?.label ?? r.scope;
    const target = r.partnerId ? partners.find((p) => p.id === r.partnerId)?.name : r.activityId ? activities.find((a) => a.id === r.activityId)?.name : "";
    return `${label}${target ? ` · ${target}` : ""}`;
  };

  const submit = async (event) => {
    event.preventDefault();
    const body = {
      scope: form.scope,
      partnerId: form.scope === "PARTNER_PER_MONTH" ? form.partnerId : null,
      activityId: form.scope === "ACTIVITY_PER_MONTH" ? form.activityId : null,
      maxUses: form.maxUses === "" ? null : Number(form.maxUses),
      active: true,
    };
    await run(saveRule({ body }), "Règle ajoutée");
  };

  return (
    <>
      <PageHeader title="Règles d'usage" subtitle="Une limite vide signifie illimité : aucune limite n'est codée en dur." />
      <ErrorBanner message={error} />

      <Card className="mb-6">
        <CardTitle>Check-in et annulation</CardTitle>
        {settings && <SettingsForm key={JSON.stringify(settings)} settings={settings} />}
      </Card>

      <Card className="mb-6">
        <CardTitle>Nouvelle règle</CardTitle>
        <form onSubmit={submit} className="mt-3 grid gap-2 md:grid-cols-4">
          <Field label="Type"><Select value={form.scope} onChange={(e) => setForm({ ...form, scope: e.target.value })} options={SCOPES} /></Field>
          {form.scope === "PARTNER_PER_MONTH" && (
            <Field label="Partenaire"><Select required value={form.partnerId} onChange={(e) => setForm({ ...form, partnerId: e.target.value })} options={partners.map((p) => ({ value: p.id, label: p.name }))} placeholder="Choisir" /></Field>
          )}
          {form.scope === "ACTIVITY_PER_MONTH" && (
            <Field label="Activité"><Select required value={form.activityId} onChange={(e) => setForm({ ...form, activityId: e.target.value })} options={activities.map((a) => ({ value: a.id, label: a.name }))} placeholder="Choisir" /></Field>
          )}
          <Field label="Maximum" hint="Vide = illimité"><Input type="number" min="1" value={form.maxUses} onChange={(e) => setForm({ ...form, maxUses: e.target.value })} /></Field>
          <Button type="submit" disabled={busy} className="self-end">Ajouter</Button>
        </form>
      </Card>

      {rules.length === 0 && <EmptyState>Aucune règle.</EmptyState>}
      <ul className="flex flex-col gap-2">
        {rules.map((r) => (
          <li key={r.id} className="flex flex-wrap items-center justify-between gap-2 rounded-xl border border-border bg-card p-3 text-sm">
            <span>{describe(r)}</span>
            <span className="flex items-center gap-2">
              <Badge tone={r.maxUses == null ? "neutral" : "info"}>{r.maxUses == null ? "Illimité" : `Max ${r.maxUses}`}</Badge>
              <Button size="sm" variant="ghost" disabled={busy} onClick={() => run(deleteRule(r.id), "Règle supprimée")}>Supprimer</Button>
            </span>
          </li>
        ))}
      </ul>
    </>
  );
}
