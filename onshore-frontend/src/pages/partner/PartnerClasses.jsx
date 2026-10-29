import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { PartnerGate } from "../../components/partner/PartnerGate";
import { EmptyState, PageHeader } from "../../components/ui/Feedback";
import { Button } from "../../components/ui/Button";
import { Badge } from "../../components/ui/Badge";
import { Field, Input, Select } from "../../components/ui/Field";
import { Modal } from "../../components/ui/Modal";
import { useAction } from "../../hooks/useAction";
import { closeClass, createClasses, deleteClass, fetchPartnerActivities, fetchPartnerClasses, reopenClass, updateClass } from "../../Redux/partner/partnerSlice";
import { addDaysIso, longDate, shortTime, todayIso } from "../../utils/format";

const EMPTY = { activityId: "", date: todayIso(), startTime: "09:00", endTime: "10:00", capacity: 10, onePassCapacity: 5, recurrenceWeeks: 1 };

function Classes({ partner }) {
  const dispatch = useDispatch();
  const { classes, activities } = useSelector((s) => s.partner);
  const { run, busy } = useAction();
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(EMPTY);

  useEffect(() => {
    const today = todayIso();
    dispatch(fetchPartnerActivities(partner.id));
    dispatch(fetchPartnerClasses({ partnerId: partner.id, from: today, to: addDaysIso(today, 60) }));
  }, [dispatch, partner.id]);

  const name = (id) => activities.find((a) => a.id === id)?.name ?? "Activité";

  const open = (klass) => {
    setEditing(klass ?? {});
    setForm(klass ? { activityId: klass.activityId, date: klass.date, startTime: klass.startTime.slice(0, 5), endTime: klass.endTime.slice(0, 5), capacity: klass.capacity, onePassCapacity: klass.onePassCapacity, recurrenceWeeks: 1 } : EMPTY);
  };

  const submit = async (event) => {
    event.preventDefault();
    const body = { ...form, capacity: Number(form.capacity), onePassCapacity: Number(form.onePassCapacity), recurrenceWeeks: Number(form.recurrenceWeeks) };
    const action = editing.id ? updateClass({ id: editing.id, body }) : createClasses(body);
    const { ok } = await run(action, "Créneau enregistré");
    if (ok) setEditing(null);
  };

  const sorted = [...classes].sort((a, b) => `${a.date}${a.startTime}`.localeCompare(`${b.date}${b.startTime}`));

  return (
    <>
      <PageHeader title="Créneaux" actions={<Button onClick={() => open(null)} disabled={activities.length === 0}>Nouveau créneau</Button>} />
      {activities.length === 0 && <EmptyState>Créez d'abord une activité.</EmptyState>}
      <ul className="flex flex-col gap-2">
        {sorted.map((c) => (
          <li key={c.id} className="flex flex-wrap items-center justify-between gap-2 rounded-xl border border-border bg-card p-3">
            <div>
              <p className="font-semibold">{name(c.activityId)}</p>
              <p className="text-xs text-muted-foreground">
                {longDate(c.date)} · {shortTime(c.startTime)}-{shortTime(c.endTime)} · {c.onePassCapacity - c.seatsLeft}/{c.onePassCapacity} places ON SHORE CLUB (capacité {c.capacity})
              </p>
            </div>
            <div className="flex items-center gap-2">
              <Badge tone={c.status === "OPEN" ? "success" : "neutral"}>{c.status === "OPEN" ? "Ouvert" : "Fermé"}</Badge>
              <Button size="sm" variant="outline" onClick={() => open(c)}>Modifier</Button>
              {c.status === "OPEN" ? (
                <Button size="sm" variant="ghost" disabled={busy} onClick={() => run(closeClass(c.id), "Créneau fermé")}>Fermer</Button>
              ) : (
                <Button size="sm" variant="ghost" disabled={busy} onClick={() => run(reopenClass(c.id), "Créneau rouvert")}>Rouvrir</Button>
              )}
              <Button size="sm" variant="ghost" disabled={busy} onClick={() => window.confirm("Supprimer ce créneau ?") && run(deleteClass(c.id), "Créneau supprimé")}>Supprimer</Button>
            </div>
          </li>
        ))}
      </ul>

      <Modal open={Boolean(editing)} title={editing?.id ? "Modifier le créneau" : "Nouveau créneau"} onClose={() => setEditing(null)}>
        <form onSubmit={submit} className="grid grid-cols-2 gap-3">
          <div className="col-span-2">
            <Field label="Activité">
              <Select required value={form.activityId} onChange={(e) => setForm({ ...form, activityId: e.target.value })} options={activities.map((a) => ({ value: a.id, label: a.name }))} placeholder="Choisir" />
            </Field>
          </div>
          <Field label="Date"><Input type="date" required value={form.date} onChange={(e) => setForm({ ...form, date: e.target.value })} /></Field>
          <Field label="Début"><Input type="time" required value={form.startTime} onChange={(e) => setForm({ ...form, startTime: e.target.value })} /></Field>
          <Field label="Fin"><Input type="time" required value={form.endTime} onChange={(e) => setForm({ ...form, endTime: e.target.value })} /></Field>
          <Field label="Capacité totale"><Input type="number" min="1" required value={form.capacity} onChange={(e) => setForm({ ...form, capacity: e.target.value })} /></Field>
          <Field label="Places ON SHORE CLUB"><Input type="number" min="0" required value={form.onePassCapacity} onChange={(e) => setForm({ ...form, onePassCapacity: e.target.value })} /></Field>
          {!editing?.id && (
            <Field label="Répéter (semaines)" hint="1 à 12">
              <Input type="number" min="1" max="12" value={form.recurrenceWeeks} onChange={(e) => setForm({ ...form, recurrenceWeeks: e.target.value })} />
            </Field>
          )}
          <Button type="submit" disabled={busy} className="col-span-2">Enregistrer</Button>
        </form>
      </Modal>
    </>
  );
}

export default function PartnerClasses() {
  return <PartnerGate>{(partner) => <Classes partner={partner} />}</PartnerGate>;
}
