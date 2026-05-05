import { Search } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useState, type FormEvent } from "react";

interface SearchBarProps {
  placeholder?: string;
  defaultValue?: string;
  onSearch: (value: string) => void;
  buttonLabel?: string;
}

export function SearchBar({ placeholder = "搜尋...", defaultValue = "", onSearch, buttonLabel = "搜尋" }: SearchBarProps) {
  const [value, setValue] = useState(defaultValue);
  const submit = (e: FormEvent) => {
    e.preventDefault();
    onSearch(value.trim());
  };
  return (
    <form onSubmit={submit} className="flex w-full gap-2">
      <div className="relative flex-1">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={value}
          onChange={(e) => setValue(e.target.value)}
          placeholder={placeholder}
          className="pl-9"
        />
      </div>
      <Button type="submit">{buttonLabel}</Button>
    </form>
  );
}
