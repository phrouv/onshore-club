import { Card } from "../ui/Card";
import { Button } from "../ui/Button";
import { Badge } from "../ui/Badge";
import { categoryImage, shortTime } from "../../utils/format";

export function ClassCard({ klass, booked, busy, onBook }) {
  const full = klass.seatsLeft <= 0;
  return (
    <Card className="flex gap-3 p-3">
      <img src={categoryImage(klass.activity.category)} alt="" className="size-20 rounded-xl object-cover" />
      <div className="flex flex-1 flex-col gap-1">
        <div className="flex items-start justify-between gap-2">
          <div>
            <p className="font-bold leading-tight">{klass.activity.name}</p>
            <p className="text-xs text-muted-foreground">
              {klass.partner.name} · {klass.partner.city}
            </p>
          </div>
          <Badge tone="info">{klass.creditsRequired} crédits</Badge>
        </div>
        <p className="text-sm">
          {shortTime(klass.startTime)} - {shortTime(klass.endTime)}
        </p>
        <div className="mt-auto flex items-center justify-between">
          <span className="text-xs text-muted-foreground">{full ? "Complet" : `${klass.seatsLeft} place(s)`}</span>
          {booked ? (
            <Badge tone="success">Réservé</Badge>
          ) : (
            <Button size="sm" variant="accent" disabled={full || busy} onClick={() => onBook(klass)}>
              Réserver
            </Button>
          )}
        </div>
      </div>
    </Card>
  );
}
