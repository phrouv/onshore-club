import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { PartnerGate } from "../../components/partner/PartnerGate";
import { EmptyState, PageHeader } from "../../components/ui/Feedback";
import { Button } from "../../components/ui/Button";
import { Card } from "../../components/ui/Card";
import { Badge } from "../../components/ui/Badge";
import { Field, Input, Select, Textarea } from "../../components/ui/Field";
import { Modal } from "../../components/ui/Modal";
import { useAction } from "../../hooks/useAction";
import { fetchPartnerActivities, saveActivity } from "../../Redux/partner/partnerSlice";
import { CATEGORIES, money } from "../../utils/format";

const EMPTY = { name: "", category: "", description: "", durationMinutes: 60, partnerPayout: 5 };

function Activities({ partner }) {
  const dispatch = useDispatch();
  const activities = useSelector((s) => s.partner.activities);
  const { run, busy } = useAction();
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(EMPTY);

  useEffect(() => {
    dispatch(fetchPartnerActivities(partner.id));
  }, [dispatch, partner.id]);

  const open = (activity) => {
    setEditing(activity ?? {});
    setForm(activity ? { name: activity.name, category: activity.category, description: activity.description ?? "", durationMinutes: activity.durationMinutes, partnerPayout: activity.partnerPayout } : EMPTY);
  };

  const submit = async (event) => {
    event.preventDefault();
    const body = { ...form, partnerId: partner.id, durationMinutes: Number(form.durationMinutes), partnerPayout: Number(form.partnerPayout), active: editing.active ?? true };
    const { ok } = await run(saveActivity({ id: editing.id, body }), "Activité enregistrée");
    if (ok) setEditing(null);
  };

  const toggle = (activity) =>
    run(saveActivity({
      id: activity.id,
      body: { partnerId: partner.id, name: activity.name, category: activity.category, description: activity.description, durationMinutes: activity.durationMinutes, partnerPayout: activity.partnerPayout, active: !activity.active },
    }));

  return (
    <>
      <PageHeader title="Activités" actions={<Button onClick={() => open(null)}>Nouvelle activité</Button>} />
      {activities.length === 0 && <EmptyState>Aucune activité pour le moment.</EmptyState>}
      <div className="grid gap-3 md:grid-cols-2">
        {activities.map((a) => (
          <Card key={a.id} className="flex flex-col gap-2">
            <div className="flex items-start justify-between">
              <div>
                <p className="font-bold">{a.name}</p>
                <p className="text-xs text-muted-foreground">{a.category} · {a.durationMinutes} min</p>
              </div>
              <Badge tone={a.active ? "success" : "neutral"}>{a.active ? "Active" : "Désactivée"}</Badge>
            </div>
            <p className="text-sm">Rémunération par check-in : <strong>{money(a.partnerPayout)}</strong></p>
            <div className="flex gap-2">
              <Button size="sm" variant="outline" onClick={() => open(a)}>Modifier</Button>
              <Button size="sm" variant="ghost" disabled={busy} onClick={() => toggle(a)}>{a.active ? "Désactiver" : "Réactiver"}</Button>
            </div>
          </Card>
        ))}
      </div>

      <Modal open={Boolean(editing)} title={editing?.id ? "Modifier l'activité" : "Nouvelle activité"} onClose={() => setEditing(null)}>
        <form onSubmit={submit} className="flex flex-col gap-3">
          <Field label="Nom"><Input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} /></Field>
          <Field label="Catégorie"><Select required value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })} options={CATEGORIES} placeholder="Choisir" /></Field>
          <Field label="Description"><Textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} /></Field>
          <Field label="Durée (minutes)"><Input type="number" min="1" required value={form.durationMinutes} onChange={(e) => setForm({ ...form, durationMinutes: e.target.value })} /></Field>
          <Field label="Rémunération par check-in (€)" hint="Montant fixe négocié, indépendant des crédits du membre.">
            <Input type="number" min="0" step="0.5" required value={form.partnerPayout} onChange={(e) => setForm({ ...form, partnerPayout: e.target.value })} />
          </Field>
          <Button type="submit" disabled={busy}>Enregistrer</Button>
        </form>
      </Modal>
    </>
  );
}

export default function PartnerActivities() {
  return <PartnerGate>{(partner) => <Activities partner={partner} />}</PartnerGate>;
}
