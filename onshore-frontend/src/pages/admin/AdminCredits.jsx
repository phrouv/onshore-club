import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { ErrorBanner, PageHeader } from "../../components/ui/Feedback";
import { Button } from "../../components/ui/Button";
import { Card, CardTitle } from "../../components/ui/Card";
import { Field, Input, Select } from "../../components/ui/Field";
import { useAction } from "../../hooks/useAction";
import { adjustCredits, fetchAdminPlans, fetchCreditSettings, fetchUsers, saveCreditSetting, savePlan } from "../../Redux/admin/adminSlice";

function PlanRow({ plan }) {
  const { run, busy } = useAction();
  const [form, setForm] = useState(plan);
  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });
  const save = () =>
    run(savePlan({ ...form, monthlyPrice: Number(form.monthlyPrice), creditsPerMonth: Number(form.creditsPerMonth) }), `Formule ${form.name} enregistrée`);
  return (
    <div className="grid grid-cols-2 items-end gap-2 md:grid-cols-6">
      <Field label="Nom"><Input value={form.name} onChange={set("name")} /></Field>
      <Field label="Prix / mois (€)"><Input type="number" value={form.monthlyPrice} onChange={set("monthlyPrice")} /></Field>
      <Field label="Crédits / mois"><Input type="number" value={form.creditsPerMonth} onChange={set("creditsPerMonth")} /></Field>
      <Field label="Positionnement"><Input value={form.positioning ?? ""} onChange={set("positioning")} /></Field>
      <Field label="Active">
        <Select value={String(form.active)} onChange={(e) => setForm({ ...form, active: e.target.value === "true" })} options={[{ value: "true", label: "Oui" }, { value: "false", label: "Non" }]} />
      </Field>
      <Button size="sm" disabled={busy} onClick={save}>Enregistrer</Button>
    </div>
  );
}

function SettingRow({ setting }) {
  const { run, busy } = useAction();
  const [form, setForm] = useState(setting);
  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });
  const save = () =>
    run(
      saveCreditSetting({
        category: setting.category,
        body: { ...form, creditsLow: Number(form.creditsLow), creditsNormal: Number(form.creditsNormal), creditsHigh: Number(form.creditsHigh), defaultPayout: Number(form.defaultPayout) },
      }),
      `${setting.category} enregistré`,
    );
  return (
    <div className="grid grid-cols-2 items-end gap-2 md:grid-cols-6">
      <p className="font-semibold">{setting.category}</p>
      <Field label="Faible"><Input type="number" value={form.creditsLow} onChange={set("creditsLow")} /></Field>
      <Field label="Normale"><Input type="number" value={form.creditsNormal} onChange={set("creditsNormal")} /></Field>
      <Field label="Forte"><Input type="number" value={form.creditsHigh} onChange={set("creditsHigh")} /></Field>
      <Field label="Rémunération par défaut (€)" hint="Jamais calculée depuis les crédits."><Input type="number" step="0.5" value={form.defaultPayout} onChange={set("defaultPayout")} /></Field>
      <Button size="sm" disabled={busy} onClick={save}>Enregistrer</Button>
    </div>
  );
}

export default function AdminCredits() {
  const dispatch = useDispatch();
  const { plans, creditSettings, users, error } = useSelector((s) => s.admin);
  const { run, busy } = useAction();
  const [adjust, setAdjust] = useState({ userId: "", amount: "", description: "" });

  useEffect(() => {
    dispatch(fetchAdminPlans());
    dispatch(fetchCreditSettings());
    dispatch(fetchUsers());
  }, [dispatch]);

  const members = users.filter((u) => u.role === "ROLE_MEMBER");

  const submitAdjust = async (event) => {
    event.preventDefault();
    const { ok } = await run(adjustCredits({ ...adjust, amount: Number(adjust.amount), type: "ADMIN_ADJUSTMENT" }), "Solde ajusté");
    if (ok) setAdjust({ userId: "", amount: "", description: "" });
  };

  return (
    <>
      <PageHeader title="Crédits & formules" />
      <ErrorBanner message={error} />

      <Card className="mb-6 flex flex-col gap-3">
        <CardTitle>Formules d'abonnement</CardTitle>
        {plans.map((p) => <PlanRow key={`${p.id}-${p.monthlyPrice}-${p.creditsPerMonth}`} plan={p} />)}
      </Card>

      <Card className="mb-6 flex flex-col gap-3">
        <CardTitle>Barème de crédits par catégorie et niveau de demande</CardTitle>
        {creditSettings.map((s) => <SettingRow key={s.category} setting={s} />)}
      </Card>

      <Card>
        <CardTitle>Ajuster le solde d'un membre</CardTitle>
        <form onSubmit={submitAdjust} className="mt-3 grid gap-2 md:grid-cols-4">
          <Field label="Membre">
            <Select required value={adjust.userId} onChange={(e) => setAdjust({ ...adjust, userId: e.target.value })} options={members.map((m) => ({ value: m.id, label: `${m.fullName} (${m.email})` }))} placeholder="Choisir" />
          </Field>
          <Field label="Crédits (+/-)"><Input type="number" required value={adjust.amount} onChange={(e) => setAdjust({ ...adjust, amount: e.target.value })} /></Field>
          <Field label="Motif"><Input value={adjust.description} onChange={(e) => setAdjust({ ...adjust, description: e.target.value })} /></Field>
          <Button type="submit" disabled={busy} className="self-end">Appliquer</Button>
        </form>
      </Card>
    </>
  );
}
